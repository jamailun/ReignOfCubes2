package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.configuration.ConfigurationsList;
import fr.jamailun.reignofcubes2.configuration.GameRules;
import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import fr.jamailun.reignofcubes2.objects.Ceremony;
import fr.jamailun.reignofcubes2.objects.GameCountdown;
import fr.jamailun.reignofcubes2.objects.Throne;
import fr.jamailun.reignofcubes2.players.PlayersManager;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.players.ScoreAddReason;
import fr.jamailun.reignofcubes2.players.ScoreRemoveReason;
import fr.jamailun.reignofcubes2.utils.Ranking;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class GameManager {

    //TODO:
    // - début de partie avec assez de joueurs.
    // - fin de partie avec score.
    // - fin de partie avec plus que 1 ou moins joueurs.

    private final PlayersManager players = new PlayersManager(this);

    @Getter private GameState state;
    @Getter private final ConfigurationsList configurationsList = new ConfigurationsList();
    @Getter private WorldConfiguration worldConfiguration;
    private World world;
    @Getter private RocPlayer king;
    @Getter private Throne throne;

    // Score
    @Getter private final Ranking<RocPlayer> ranking = new Ranking<>(RocPlayer::getScore);
    private BukkitTask scoreTimer;

    // Countdown
    @Getter private GameCountdown countdown;

    GameManager() {
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
                throne.reset();
                throne = null;
            }
            world = null;
            ranking.clear();
            state = GameState.NOT_CONFIGURED;
            return true;
        }
        if(!configuration.isValid()) {
            ReignOfCubes2.error("Could not load invalid configuration " + configuration);
            return false;
        }
        worldConfiguration = configuration;
        throne = worldConfiguration.generateThrone(this);
        world = Bukkit.getWorld(worldConfiguration.getWorldName());
        state = GameState.WAITING;
        return true;
    }

    public void playerJoinsServer(Player p) {
        RocPlayer player = players.join(p);
        broadcast("event.joined", player.getName());

        testShouldStartGame();
    }

    public void playerLeftServer(Player p) {
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
    }

    private void testShouldStartGame() {
        if(state != GameState.WAITING) return;
        if(worldConfiguration == null || ! worldConfiguration.isValid()) return;
        int minPlayers = getRules().getPlayerCountMin();
        if(players.size() >= minPlayers) {
            ReignOfCubes2.info("Enough players ! Will start the game now.");
            startCountdown();
        }
    }

    public void playerDies(@Nonnull RocPlayer victim) {
        RocPlayer killer = victim.getLastDamager();

        if(killer == null) {
            if(victim.isKing()) {
                setKing(null);
                broadcast("event.king.death", victim.getName());
            } else {
                broadcast("event.death.alone", victim.getName());
            }
            victim.removeScore(getRules().getScoreDeathPenalty(), ScoreRemoveReason.DEATH_PENALTY);
            ranking.update(victim);
            return;
        }

        // broadcast + king switch
        if(killer.isKing()) {
            broadcast("event.death.killed-as-king", victim.getName(), killer.getName());
            //TODO points bonus if king ?
        } else if(victim.isKing()) {
            broadcast("event.death.killed-king", victim.getName(), killer.getName());
            setKing(killer);
        } else {
            broadcast("event.death.killed", victim.getName(), killer.getName());
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
                king.setKing(false);
                king = null;
            }
            return;
        }
        // remove king from old king.
        if(king != null) {
            assert king != player : "The king wanted to become the king again...";
            king.setKing(false);
        }

        // set new king
        player.setKing(true);
        king = player;
        broadcast("event.king.new", king.getName());

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
        player.sendMessage("throne.end-ceremony");
        setKing(player);
    }

    public GameRules getRules() {
        return worldConfiguration.getRules();
    }

    private void testShouldStart() {
        if(state != GameState.WAITING)
            return;

        if(players.size() >= getRules().getPlayerCountMin()) {
            startCountdown();
        }
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

        // Remove countdown
        if(countdown != null) {
            countdown.cancel();
        }

        // TP players to a spawn-point.
        players.start(worldConfiguration.generateSpawns());
        players.updateRanking(ranking);

        // Start score timer
        scoreTimer = ReignOfCubes2.runTaskTimer(() -> {
            if(hasKing()) {
                king.addScore(getRules().getScoreKingPerSecond(), ScoreAddReason.KING_EVERY_SECOND);
                ranking.update(king);
            }
        }, 1);

        broadcast("game.start");
    }

    public void stop() {
        // Not playing ? Why are we here ??
        if(state != GameState.PLAYING) {
            ReignOfCubes2.warning("Useless GameManager#stop(), because state is " + state);
            return;
        }

        // Reset players, score, king and state.
        scoreTimer.cancel();
        scoreTimer = null;
        players.clearOfflines();
        ranking.clear();
        if(king != null) {
            king.setKing(false);
            king = null;
        }
        throne.reset();
        throne = null;
        state = GameState.WAITING;

        // Message and go back to spawn
        //TODO message, TP players, ...
        broadcast("game.end");
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
        //TODO !

        // Cancel timer
        throne.reset();
        scoreTimer.cancel();
    }

    public List<RocPlayer> playersList() {
        return StreamSupport.stream(players.spliterator(), false).toList();
    }

    public Optional<RocPlayer> findPlayer(String playerName) {
        return StreamSupport.stream(players.spliterator(), false)
                .filter(p -> p.getName().equalsIgnoreCase(playerName))
                .findFirst();
    }

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
        }
    }
}
