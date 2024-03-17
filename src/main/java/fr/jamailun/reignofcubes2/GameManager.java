package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.configuration.ConfigurationsList;
import fr.jamailun.reignofcubes2.configuration.GameRules;
import fr.jamailun.reignofcubes2.configuration.SoundsLibrary;
import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import fr.jamailun.reignofcubes2.configuration.pickups.PickupConfigEntry;
import fr.jamailun.reignofcubes2.messages.Messages;
import fr.jamailun.reignofcubes2.music.MusicManager;
import fr.jamailun.reignofcubes2.music.MusicType;
import fr.jamailun.reignofcubes2.objects.Ceremony;
import fr.jamailun.reignofcubes2.objects.GameCountdown;
import fr.jamailun.reignofcubes2.objects.Throne;
import fr.jamailun.reignofcubes2.pickup.PickupsManager;
import fr.jamailun.reignofcubes2.players.PlayersManager;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.players.ScoreAddReason;
import fr.jamailun.reignofcubes2.players.ScoreRemoveReason;
import fr.jamailun.reignofcubes2.utils.Ranking;
import fr.jamailun.reignofcubes2.utils.WorldSetter;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GameManager {

    private final PlayersManager players = new PlayersManager(this);
    @Getter private final MusicManager musics;

    @Getter private GameState state;
    @Getter private final ConfigurationsList configurationsList = new ConfigurationsList();
    @Getter private WorldConfiguration worldConfiguration;
    private World world;
    @Getter private RocPlayer king;
    @Getter private Throne throne;

    // Score
    @Getter private final Ranking<RocPlayer> ranking = new Ranking<>(RocPlayer::getScore);
    private final PickupsManager pickups = new PickupsManager(() -> worldConfiguration.getPickupConfiguration().pickRandom());
    private BukkitTask gameTimer;

    // Countdown
    @Getter private GameCountdown countdown;

    // is over with a victory ?
    private boolean isVictory;

    GameManager(MusicManager musics) {
        this.musics = musics;
        loadConfiguration(configurationsList.getDefault());
    }

    public boolean loadConfiguration(@Nullable WorldConfiguration configuration) {
        if(isPlaying()) {
            ReignOfCubes2.error("Cannot change configuration while playing !");
            return false;
        }
        if(configuration == null) {
            ReignOfCubes2.warning("Setting GameManager configuration as null.");
            worldConfiguration = null;
            if(throne != null) {
                throne.resetCeremony();
                throne = null;
            }
            world = null;
            ranking.clear();
            state = GameState.NOT_CONFIGURED;
            pickups.purgeAndClear();
            return true;
        }
        if(!configuration.isValid()) {
            ReignOfCubes2.error("Could not load invalid configuration " + configuration);
            return false;
        }
        worldConfiguration = configuration;
        throne = worldConfiguration.generateThrone(this);
        world = Bukkit.getWorld(worldConfiguration.getWorldName());
        assert world != null;
        WorldSetter.configure(world);
        state = GameState.WAITING;
        pickups.regenerateAll(configuration.getGeneratorsList(world), getRules().getGeneratorFrequency());
        return true;
    }

    public void playerJoinsServer(Player p) {
        // The game already started : try to rejoin
        if(isPlaying()) {
            // If existed, re-join the game
            if(players.exists(p)) {
                playerRejoins(p);
                assert p.equals(players.get(p).getPlayer());
                return;
            }

            // A player connected while the game was on. Spectator !
            ReignOfCubes2.runTaskLater(() -> {
                p.setGameMode(GameMode.SPECTATOR);
                p.teleport(players.iterator().next().getPlayer());
            }, 0.5);

            // Join-messages
            String msg = Messages.format("fr", "event.joined-spectator-self");
            p.sendMessage(Messages.parseComponent(msg));
            broadcast("event.joined-spectator", p.getName());
            return;
        }

        RocPlayer player = players.join(p);
        broadcast("event.joined", player.getName());
        playSound(SoundsLibrary.PLAYER_JOINED);

        // Go to lobby on join
        if(worldConfiguration != null && worldConfiguration.isValid()) {
            makePlayerJoinsLobby(p);
        }

        // Add to musics
        musics.addPlayer(p, MusicType.LOBBY);

        // Test if the game should start.
        testShouldStartGame();
    }

    private void makePlayerJoinsLobby(Player p) {
        // Teleport to lobby
        p.teleport(worldConfiguration.getLobby());

        // Clear inventory & adventure mode
        p.getInventory().clear();
        p.setGameMode(GameMode.ADVENTURE);

        // Heal + saturation
        p.setHealth(Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        p.setFoodLevel(20);
    }

    public void playerLeftServer(Player p) {
        musics.removePlayer(p);
        players.maybeLeave(p);
        if(!players.exists(p)) {
            return;
        }
        RocPlayer player = players.get(p);
        if(player.isKing()) {
            // not anymore
            players.broadcast("event.left-king", p.getName());
            player.setKing(false);
            setKing(null);
        } else {
            players.broadcast("event.left", p.getName());
        }

        // If he leaves the server, remove it from the throne !
        if(throne != null) {
            throne.leaves(player);
        }

        // After one second, test if no/one player are remaining.
        ReignOfCubes2.runTaskLater(this::testGameOverMissingPlayers, 1);
    }

    private void testGameOverMissingPlayers() {
        if(!isPlaying()) return;
        int remainingPlayers = getOnlinePlayersCount();
        ReignOfCubes2.info("A player left. Remaining online = " + remainingPlayers);
        if(isPlaying() && remainingPlayers <= 1) {
            if(remainingPlayers == 0) {
                ReignOfCubes2.info("No players remaining. Stopping game.");
                stop();
            } else {
                RocPlayer alone = players()
                        .filter(rp -> rp.getPlayer().isOnline())
                        .findFirst().orElseThrow();
                alone.sendMessage("game.alone");
                victory(alone);
            }
        }
    }

    private void testShouldStartGame() {
        if(state != GameState.WAITING) return;
        if(worldConfiguration == null || ! worldConfiguration.isValid()) return;
        int minPlayers = getRules().getPlayerCountMin();
        if(players.size() >= minPlayers) {
            ReignOfCubes2.info("Enough players (" + players.size() + " >= " + minPlayers + ") ! Will start the game now.");
            startCountdown();
        }
    }

    public void playerDies(@Nonnull RocPlayer victim) {
        RocPlayer killer = victim.getLastDamager();

        if(killer == null) {
            if(victim.isKing()) {
                setKing(null);
                // le setKing va faire les sons dans ce cas
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
            setKing(killer);
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

    private void setKing(RocPlayer player) {
        if(player == null) {
            if(king != null) {
                broadcast("event.king.death", king.getName());
                king.playSound(SoundsLibrary.DEAD_AS_KING);
                playSound(SoundsLibrary.KING_KILLED);

                king.setKing(false);
                king = null;
            }
            return;
        }
        // remove king from old king.
        if(king != null) {
            assert king != player : "The king wanted to become the king again...";
            musics.addPlayer(king.getPlayer(), MusicType.PLAY_NORMAL);
            king.setKing(false);
        }

        // set new king
        player.setKing(true);
        king = player;
        broadcast("event.king.new", king.getName());
        musics.addPlayer(king.getPlayer(), MusicType.PLAY_KING);

        // Add score
        king.addScore(getRules().getScoreKingBonus(), ScoreAddReason.KING_FLAT_BONUS);
        ranking.update(king);
    }

    public boolean isInWorld(World w) {
        if(state == GameState.NOT_CONFIGURED)
            return false;
        return this.world.equals(w);
    }

    public boolean isPlaying() {
        return state == GameState.PLAYING;
    }
    public boolean isCountdown() {
        return state == GameState.COUNT_DOWN;
    }

    public @Nullable RocPlayer toPlayer(@Nullable Player p) {
        if(p == null) return null;
        if(isPlaying()) {
            if(players.exists(p))
                return players.get(p);
            return null;
        }
        return players.join(p);
    }

    public boolean hasKing() {
        return king != null;
    }

    public void ceremonyIsOver(RocPlayer player) {
        assert king == null : "How can a ceremony be ver if there is already a king ?";
        // GG message
        player.sendMessage("throne.end-ceremony");
        // set king
        setKing(player);
        // remove ceremony stuff
        throne.resetCeremony();
    }

    public GameRules getRules() {
        return worldConfiguration.getRules();
    }

    public void startCountdown() {
        if(isPlaying()) {
            ReignOfCubes2.warning("Tried to start countdown... Game already started.");
            return;
        }
        assert state == GameState.WAITING;
        state = GameState.COUNT_DOWN;
        if(countdown != null) countdown.cancel();

        // Start the countdown
        countdown = new GameCountdown(this);
    }

    public void stopCountdown() {
        if(state != GameState.COUNT_DOWN) {
            ReignOfCubes2.warning("GameManager#stopCountdown() called in state " + state);
            return;
        }
        if(countdown == null) {
            ReignOfCubes2.warning("GameManager#stopCountdown() has a... null countdown ??");
            return;
        }
        countdown.cancel();
        state = GameState.WAITING;
    }

    public void start() {
        assert state != GameState.NOT_CONFIGURED && state != GameState.PLAYING;
        assert worldConfiguration != null && worldConfiguration.isValid();
        state = GameState.PLAYING;
        isVictory = false;
        pickups.start(worldConfiguration.getGeneratorsList(world), getRules().getGeneratorFrequency());

        // Remove countdown
        if(countdown != null) {
            countdown.cancel();
        }

        // TP players to a spawn-point.
        players.start(worldConfiguration.generateSpawns());
        players.updateRanking(ranking);

        // Start score timer
        gameTimer = ReignOfCubes2.runTaskTimer(() -> {
            if(hasKing()) {
                king.addScore(getRules().getScoreKingPerSecond(), ScoreAddReason.KING_EVERY_SECOND);
                king.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0, false, false, true));
                if(throne != null && throne.isAlreadyInside(king)) {
                    king.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 0, false, false, true));
                }
                ranking.update(king);
            }
        }, 1);

        // Clear entities after loading (
        ReignOfCubes2.runTaskLater(() -> {
            ReignOfCubes2.info("Clear all entities of world.");
            world.getEntities().stream()
                    .filter(e -> !(e instanceof Player))
                    .forEach(Entity::remove);
        }, 1.5);

        // Sounds and messages
        playSound(SoundsLibrary.GAME_STARTED_1);
        for(RocPlayer pl : players) {
            musics.addPlayer(pl.getPlayer(), MusicType.PLAY_NORMAL);
        }

        broadcast("game.start");
        ReignOfCubes2.info("Game started.");
    }

    public void stop() {
        // Not playing ? Why are we here ??
        if(state != GameState.PLAYING) {
            ReignOfCubes2.warning("Useless GameManager#stop(), because state is " + state);
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
        throne.resetCeremony();
        throne = null;
        state = GameState.WAITING;
        isVictory = false;
        pickups.purgeAndStop();

        // Message and go back to spawn
        ReignOfCubes2.info("Game stopped.");
        for(RocPlayer pl : players) {
            musics.addPlayer(pl.getPlayer(), MusicType.LOBBY);
        }
        players.backToLobby();

        // Reset throne
        loadConfiguration(configurationsList.getDefault());
        ReignOfCubes2.runTaskLater(this::testShouldStartGame, 2);
    }

    /**
     * To call before shutdown : clear elements.
     */
    public void purge() {
        // Reset all
        pickups.purgeAndStop();
    }

    public void broadcast(String entry, Object... args) {
        players.broadcast(entry, args);
    }

    public int getPlayersCount() {
        return players.size();
    }

    public @Nullable Ceremony getCeremony() {
        if(throne == null || ! throne.hasCeremony())
            return null;
        return throne.getCeremony();
    }

    public void checkVictory(@Nonnull RocPlayer player) {
        assert isPlaying();

        if(player.getScore() >= getRules().getScoreGoal()) {
            victory(player);
        }
    }

    private void victory(RocPlayer player) {
        if(player == null) {
            ReignOfCubes2.warning("Unexpected NULL player for victory.");
        } else {
            broadcast("game.end-victory", player.getName(), player.getScore());
            ReignOfCubes2.info("Player " + player.getName() + " won.");
        }

        // Cancel stuff
        throne.resetCeremony();
        gameTimer.cancel();
        pickups.purgeAndStop();

        // set victory
        isVictory = true;

        // In X seconds, stop and restart
        ReignOfCubes2.runTaskLater(this::stop, 7);
    }

    public Stream<RocPlayer> players() {
        return StreamSupport.stream(players.spliterator(), false);
    }

    public Optional<RocPlayer> findPlayer(String playerName) {
        return players()
                .filter(p -> p.getName().equalsIgnoreCase(playerName))
                .findFirst();
    }

    public int getOnlinePlayersCount() {
        return (int) players().filter(p -> p.getPlayer().isOnline()).count();
    }

    public void playSound(SoundsLibrary.SoundEntry entry) {
        playSound(entry.sound(), 5f, entry.pitch());
    }

    public void playSound(Sound sound, float volume, float pitch) {
        players().forEach(p -> p.playSound(sound, volume, pitch));
    }


    public void playerRejoins(Player p) {
        assert players.exists(p) : "Not supposed to rejoin if doesn't exist before.";
        RocPlayer player = players.get(p);
        player.changePlayerInstance(p);

        // clear stuff anyway, and teleport
        ReignOfCubes2.info("Player re-joined : " + p.getName() + ".");
        p.teleport(getWorldConfiguration().getSafeSpawn(true));
        player.respawned();
        musics.addPlayer(p, MusicType.PLAY_NORMAL);
    }

    /**
     * Public access to cheats. Allows disabling easily.
     */
    public final Cheat cheat = new Cheat();
    public class Cheat {
        public void forceKing(@Nullable RocPlayer player) {
            if(player == null) {
                setKing(null);
                return;
            }
            setKing(player);
        }

        public void forceScore(@Nonnull RocPlayer player, int score) {
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

    public Optional<PickupConfigEntry> didPickedUpItem(Item item) {
        return pickups.tryPickupItem(item);
    }
}
