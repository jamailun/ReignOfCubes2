package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.api.GameManager;
import fr.jamailun.reignofcubes2.api.GameState;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.configuration.RocConfigurationsManager;
import fr.jamailun.reignofcubes2.api.events.game.GameStopEvent;
import fr.jamailun.reignofcubes2.api.events.player.KingChangedEvent;
import fr.jamailun.reignofcubes2.api.events.player.PlayerScoreChangedEvent;
import fr.jamailun.reignofcubes2.api.events.player.RocPlayerDeathEvent;
import fr.jamailun.reignofcubes2.api.gameplay.*;
import fr.jamailun.reignofcubes2.api.music.MusicManager;
import fr.jamailun.reignofcubes2.api.players.*;
import fr.jamailun.reignofcubes2.api.sounds.SoundEffect;
import fr.jamailun.reignofcubes2.api.music.MusicType;
import fr.jamailun.reignofcubes2.api.utils.Ranking;
import fr.jamailun.reignofcubes2.configuration.GameConfiguration;
import fr.jamailun.reignofcubes2.configuration.GameConfigurationsManager;
import fr.jamailun.reignofcubes2.configuration.GameRules;
import fr.jamailun.reignofcubes2.configuration.pickups.PickupConfigEntry;
import fr.jamailun.reignofcubes2.configuration.sections.GameRulesSection;
import fr.jamailun.reignofcubes2.configuration.sections.TagsConfigurationSection;
import fr.jamailun.reignofcubes2.messages.Messages;
import fr.jamailun.reignofcubes2.music.MusicManagerImpl;
import fr.jamailun.reignofcubes2.gameplay.GameCountdownImpl;
import fr.jamailun.reignofcubes2.music.SoundsLibrary;
import fr.jamailun.reignofcubes2.pickup.PickupsManager;
import fr.jamailun.reignofcubes2.players.PlayersManager;
import fr.jamailun.reignofcubes2.players.RocPlayerImpl;
import fr.jamailun.reignofcubes2.utils.WorldSetter;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GameManagerImpl implements GameManager {

    private final PlayersManager players = new PlayersManager(this);
    private final MusicManagerImpl musicManager;

    @Getter private GameState state;
    @Getter private final GameConfigurationsManager configsManager = new GameConfigurationsManager();
    private GameConfiguration worldConfiguration;
    private World world;
    @Getter private RocPlayer king;
    @Getter private Throne throne;

    // Score
    @Getter private final Ranking<RocPlayer> ranking = new Ranking<>(RocPlayer::getScore);
    private BukkitTask gameTimer;

    // Countdown
    @Getter private GameCountdown countdown;

    // is over with a victory ?
    private boolean isVictory;

    GameManagerImpl(MusicManagerImpl musicManager) {
        this.musicManager = musicManager;
        loadConfiguration(configsManager.getDefault());
    }

    public static @NotNull GameManagerImpl instance() {
        return null;//FIXME
    }

    public boolean loadConfiguration(@Nullable GameConfiguration configuration) {
        if(isStatePlaying()) {
            ReignOfCubes2.logError("Cannot change configuration while playing !");
            return false;
        }
        if(configuration == null) {
            ReignOfCubes2.logWarning("Setting GameManager configuration as null.");
            worldConfiguration = null;
            if(throne != null) {
                throne.resetCapture();
                throne = null;
            }
            world = null;
            ranking.clear();
            state = GameState.NOT_CONFIGURED;
            pickups.purgeAndClear();
            return true;
        }
        if(!configuration.isPlayable()) {
            ReignOfCubes2.logError("Could not load invalid configuration " + configuration);
            return false;
        }
        worldConfiguration = configuration;
        throne = worldConfiguration.generateThrone();
        world = Bukkit.getWorld(worldConfiguration.getWorldName());
        assert world != null;
        WorldSetter.configure(world);
        state = GameState.WAITING;
        pickups.regenerateAll(configuration.getGeneratorsList(world), getRules().getGeneratorFrequency());
        return true;
    }

    public void playerJoinsServer(@NotNull Player p) {
        // The game already started : try to rejoin
        if(isStatePlaying()) {
            // If existed, re-join the game
            if(players.exists(p)) {
                playerRejoins(p);
                assert p.equals(players.get(p).getPlayer());
                return;
            }

            // A player connected while the game was on. Spectator !
            MainROC2.runTaskLater(() -> {
                p.setGameMode(GameMode.SPECTATOR);
                p.teleport(players.iterator().next().getPlayer());
            }, 0.5);

            // Join-messages
            String msg = Messages.format("fr", "event.joined-spectator-self");
            p.sendMessage(Messages.parseComponent(msg));
            broadcast("event.joined-spectator", p.getName());
            return;
        }

        RocPlayerImpl player = players.join(p);
        broadcast("event.joined", player.getName());
        playSound(SoundsLibrary.PLAYER_JOINED);

        // Go to lobby on join
        if(worldConfiguration != null && worldConfiguration.isPlayable()) {
            makePlayerJoinsLobby(p);
        }

        // Add to musics
        musicManager.addPlayer(p, MusicType.LOBBY);

        // Test if the game should start.
        testShouldStartGame();
    }

    private void makePlayerJoinsLobby(Player p) {
        // Teleport to lobby
        p.teleport(worldConfiguration.lobby());

        // Clear inventory & adventure mode
        p.getInventory().clear();
        p.setGameMode(GameMode.ADVENTURE);

        // Heal + saturation
        p.setHealth(Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        p.setFoodLevel(20);
    }

    public void playerLeftServer(Player p) {
        musicManager.removePlayer(p);
        players.maybeLeave(p);
        if(!players.exists(p)) {
            return;
        }
        RocPlayerImpl player = players.get(p);
        if(player.isKing()) {
            // not anymore
            players.broadcast("event.left-king", p.getName());
            player.setKing(false);
            Bukkit.getPluginManager().callEvent(new KingChangedEvent(null, player, KingChangedReason.OLD_KING_LEFT));
        } else {
            players.broadcast("event.left", p.getName());
        }

        // If he leaves the server, remove it from the throne !
        if(throne != null) {
            throne.leaves(player);
        }

        // After one second, test if no/one player are remaining.
        MainROC2.runTaskLater(this::testGameOverMissingPlayers, 1);
        player.setTag(null);
    }

    private void testGameOverMissingPlayers() {
        if(!isStatePlaying()) return;
        int remainingPlayers = getOnlinePlayersCount();
        ReignOfCubes2.logInfo("A player left. Remaining online = " + remainingPlayers);
        if(isStatePlaying() && remainingPlayers <= 1) {
            if(remainingPlayers == 0) {
                ReignOfCubes2.logInfo("No players remaining. Stopping game.");
                stop();
            } else {
                RocPlayerImpl alone = players()
                        .filter(rp -> rp.getPlayer().isOnline())
                        .findFirst().orElseThrow();
                alone.sendMessage("game.alone");
                victory(alone);
            }
        }
    }

    private void testShouldStartGame() {
        if(state != GameState.WAITING) return;
        if(worldConfiguration == null || ! worldConfiguration.isPlayable()) return;
        int minPlayers = getRules().getPlayerCountMin();
        if(players.size() >= minPlayers) {
            ReignOfCubes2.logInfo("Enough players (" + players.size() + " >= " + minPlayers + ") ! Will start the game now.");
            startCountdown();
        }
    }

    @EventHandler
    void onPlayerDies(@Nonnull RocPlayerDeathEvent event) {
        RocPlayer victim = event.getPlayer();
        RocPlayer killer = event.getKiller();

        // No killer : suicide
        if(killer == null) {
            if(victim.isKing()) {
                Bukkit.getPluginManager().callEvent(new KingChangedEvent(null, victim, KingChangedReason.OLD_KING_DIED_ALONE));
            } else {
                broadcast("event.death.alone", victim.getName());
                victim.playSound(SoundsLibrary.DEAD);
            }
            victim.removeScore(getRules().getScoreDeathPenalty(), ScoreRemoveReason.DEATH_PENALTY);
            ranking.update(victim);
            return;
        }

        // broadcast + king switch
        if(killer.isKing()) {
            broadcast("event.death.killed-as-king", victim.getName(), killer.getName());
            victim.playSound(SoundsLibrary.DEAD);
            killer.playSound(SoundsLibrary.KILLED_AS_KING);
        } else if(victim.isKing()) {
            broadcast("event.death.killed-king", victim.getName(), killer.getName());
            Bukkit.getPluginManager().callEvent(new KingChangedEvent(killer, victim, KingChangedReason.OLD_KING_KILLED));
            victim.playSound(SoundsLibrary.DEAD_AS_KING);
            killer.playSound(SoundsLibrary.KILLED_KING);
            playSound(SoundsLibrary.KING_KILLED);
        } else {
            broadcast("event.death.killed", victim.getName(), killer.getName());
            victim.playSound(SoundsLibrary.DEAD);
            killer.playSound(SoundsLibrary.KILLED);
        }

        // Points
        killer.addScore(getRules().getScoreKillFlat(), ScoreAddReason.KILL_FLAT);
        victim.removeScore(getRules().getScoreDeathPenalty(), ScoreRemoveReason.DEATH_PENALTY);

        int stoleVictimScore = (int) (victim.getScore() * getRules().getScoreKillSteal());
        if(stoleVictimScore > 0) {
            killer.addScore(stoleVictimScore, ScoreAddReason.KILL_STEAL);
            victim.removeScore(stoleVictimScore, ScoreRemoveReason.KILL_STEAL);
        }

        killer.sendMessage("score.base.bilan", killer.getScore());
        victim.sendMessage("score.base.bilan", victim.getScore());
        ranking.update(victim, killer);
    }

    @EventHandler(ignoreCancelled = true)
    void onKingChanged(@NotNull KingChangedEvent event) {
        RocPlayer oldKing = event.getOldKing();
        RocPlayer newKing = event.getNewKing();

        // Always do those actions
        if(oldKing != null) {
            musicManager.addPlayer(oldKing.getPlayer(), MusicType.PLAY_NORMAL);
            oldKing.setKing(false);
            oldKing.playSound(SoundsLibrary.DEAD_AS_KING);
        }
        if(newKing != null) {
            musicManager.addPlayer(newKing.getPlayer(), MusicType.PLAY_KING);
            newKing.setKing(true);
            broadcast("event.king.new", newKing.getName());
            newKing.addScore(getRules().getScoreKingBonus(), ScoreAddReason.KING_FLAT_BONUS);
            ranking.update(newKing);
        }
        king = newKing;

        // King killed himself
        if (event.getReason().equals(KingChangedReason.OLD_KING_DIED_ALONE)) {
            assert oldKing != null;

            broadcast("event.king.death", oldKing.getName());
            playSound(SoundsLibrary.KING_KILLED);

            return;
        }
    }

    public boolean isInWorld(World w) {
        if(state == GameState.NOT_CONFIGURED)
            return false;
        return this.world.equals(w);
    }

    @Override
    public boolean isStatePlaying() {
        return state == GameState.PLAYING;
    }

    @Override
    public boolean isStateCountdown() {
        return state == GameState.COUNT_DOWN;
    }

    @Override
    public boolean hasKing() {
        return king != null;
    }

    public void throneCaptureCompleted(@NotNull RocPlayer player) {
        throne.resetCapture();
        // Propagate
        KingChangedEvent event = new KingChangedEvent(king, player, KingChangedReason.CROWNING_ON_THRONE);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Shortcut to the game rules of the configuration.
     * @return an instance of the game rules.
     * @throws NullPointerException if no configuration has been set.
     */
    public GameRules getRules() {
        return worldConfiguration.getRulesHolder();
    }

    public void startCountdown() {
        if(isStatePlaying()) {
            ReignOfCubes2.logWarning("Tried to start countdown... Game already started.");
            return;
        }
        assert state == GameState.WAITING;
        state = GameState.COUNT_DOWN;
        if(countdown != null) countdown.cancel();

        // Start the countdown
        countdown = new GameCountdownImpl(this);
    }

    public void stopCountdown() {
        if(state != GameState.COUNT_DOWN) {
            ReignOfCubes2.logWarning("GameManager#stopCountdown() called in state " + state);
            return;
        }
        if(countdown == null) {
            ReignOfCubes2.logWarning("GameManager#stopCountdown() has a... null countdown ??");
            return;
        }
        countdown.cancel();
        state = GameState.WAITING;
    }

    public void start() {
        if(state == GameState.NOT_CONFIGURED) throw new RuntimeException("Cannot start game, if not configured.");
        if(state == GameState.PLAYING) {

        }
        assert state != GameState.NOT_CONFIGURED && state != GameState.PLAYING;
        assert worldConfiguration != null && worldConfiguration.isPlayable();
        state = GameState.PLAYING;
        isVictory = false;

        // Remove countdown
        if(countdown != null) {
            countdown.cancel();
        }

        // TP players to a spawn-point.
        players.start(worldConfiguration.generateSpawns());
        players.updateRanking(ranking);

        // Start score timer
        gameTimer = MainROC2.runTaskTimer(() -> {
            if(hasKing()) {
                king.addScore(getRules().getScoreKingPerSecond(), ScoreAddReason.KING_EVERY_SECOND);
                king.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0, false, false, true));
                if(throne != null && throne.isAlreadyInside(king)) {
                    king.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 0, false, false, true));
                }
                ranking.update(king);
            }
        }, 1);

        // Clear entities after loading (
        MainROC2.runTaskLater(() -> {
            ReignOfCubes2.logInfo("Clear all entities of world.");
            world.getEntities().stream()
                    .filter(e -> !(e instanceof Player))
                    .forEach(Entity::remove);
        }, 1.5);

        // Sounds and messages
        playSound(SoundsLibrary.GAME_STARTED_1);
        for(RocPlayerImpl pl : players) {
            musicManager.addPlayer(pl.getPlayer(), MusicType.PLAY_NORMAL);
        }

        broadcast("game.start");
        ReignOfCubes2.logInfo("Game started.");
    }

    public void stop() {
        // Not playing ? Why are we here ??
        if(state != GameState.PLAYING) {
            ReignOfCubes2.logWarning("Useless GameManager#stop(), because state is " + state);
            return;
        }

        if( ! isVictory) {
            broadcast("game.end");
        }

        // Reset players, score, king and state.
        gameTimer.cancel();
        gameTimer = null;
        players.clearOfflines();
        ranking.clear();
        if(king != null) {
            king.setKing(false);
            king = null;
        }
        throne.resetCapture();
        throne = null;
        state = GameState.WAITING;
        isVictory = false;
        pickups.purgeAndStop();

        // Message and go back to spawn
        ReignOfCubes2.logInfo("Game stopped.");
        for(RocPlayerImpl pl : players) {
            musicManager.addPlayer(pl.getPlayer(), MusicType.LOBBY);
            pl.reset();
        }
        players.backToLobby();

        // Reset throne
        loadConfiguration(configsManager.getDefault());
        MainROC2.runTaskLater(this::testShouldStartGame, 2);
    }

    @Override
    public void broadcast(String entry, Object... args) {
        players.broadcast(entry, args);
    }

    /**
     * Test if the game as enough players.
     * @return true if the players size is greater than the minimum required amount.
     */
    public boolean asEnoughPlayers() {
        return getOnlinePlayersCount() >= getRules().getPlayerCountMin();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onScoreChanged(@NotNull PlayerScoreChangedEvent event) {
        if(state != GameState.PLAYING) return;

        // update rankings
        ranking.update(event.getPlayer());

        // Check for victory.
        if(event.getPlayer().getScore() >= getRules().getScoreGoal()) {
            victory(event.getPlayer());
        }
    }

    private void victory(@NotNull RocPlayer player) {
        // Print messages
        broadcast("game.end-victory", player.getName(), player.getScore());
        ReignOfCubes2.logInfo("Player " + player.getName() + " won.");

        // Propagates
        Bukkit.getPluginManager().callEvent(new GameStopEvent(player, ranking));

        // Cancel stuff
        //TODO clean that
        throne.resetCapture();
        gameTimer.cancel();

        // set victory
        isVictory = true;

        // In X seconds, stop and restart
        MainROC2.runTaskLater(this::stop, 7);
    }

    public Stream<RocPlayerImpl> players() {
        return StreamSupport.stream(players.spliterator(), false);
    }

    public Optional<RocPlayerImpl> findPlayer(@NotNull String playerName) {
        return players()
                .filter(p -> p.getName().equalsIgnoreCase(playerName))
                .findFirst();
    }

    @Override
    public int getOnlinePlayersCount() {
        return (int) players().filter(p -> p.getPlayer().isOnline()).count();
    }

    @Override
    public void playSound(SoundEffect effect) {
        players().forEach(p -> p.playSound(effect.sound(), effect.volume(), effect.pitch()));
    }

    public void playerRejoins(Player p) {
        assert players.exists(p) : "Not supposed to rejoin if doesn't exist before.";
        RocPlayerImpl player = players.get(p);
        player.changePlayerInstance(p);

        // clear stuff anyway, and teleport
        ReignOfCubes2.logInfo("Player re-joined : " + p.getName() + ".");
        p.teleport(getActiveConfiguration().get(true));
        player.respawned();
        musicManager.addPlayer(p, MusicType.PLAY_NORMAL);
    }

    /**
     * Public access to cheats. Allows disabling easily.
     */
    public final Cheat cheat = new Cheat();

    public Collection<RocPlayer> getPlayers() {
        return players.list();
    }

    /**
     * Handle "administration tools".
     */
    public class Cheat {
        public void forceKing(@Nullable RocPlayerImpl player) {
            Bukkit.getPluginManager().callEvent(new KingChangedEvent(player, king, KingChangedReason.ADMINISTRATOR));
        }

        public void forceScore(@Nonnull RocPlayerImpl player, int score) {
            if(score < 0) score = 0;
            int currentScore = player.getScore();
            if(score == currentScore) return;

            if(currentScore > score) {
                player.removeScore(currentScore - score, ScoreRemoveReason.ADMINISTRATOR);
            } else {
                player.addScore(score - currentScore, ScoreAddReason.ADMINISTRATOR);
            }
            ranking.update(player);
        }
    }

    @Override
    public @NotNull MusicManager getMusicManager() {
        return musicManager;
    }

    @Override
    public @NotNull RocConfigurationsManager getConfigurations() {
        return configsManager;
    }

    @Override
    public GameConfiguration getActiveConfiguration() {
        return worldConfiguration;
    }
}
