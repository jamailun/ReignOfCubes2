package fr.jamailun.reignofcubes2.state;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.RocScheduler;
import fr.jamailun.reignofcubes2.api.GameState;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.configuration.RocConfiguration;
import fr.jamailun.reignofcubes2.api.configuration.UnknownConfigurationException;
import fr.jamailun.reignofcubes2.api.events.game.ConfigurationChangedEvent;
import fr.jamailun.reignofcubes2.api.events.game.CountdownStartEvent;
import fr.jamailun.reignofcubes2.api.events.game.GameStartEvent;
import fr.jamailun.reignofcubes2.api.events.game.GameStopEvent;
import fr.jamailun.reignofcubes2.api.events.player.KingChangedEvent;
import fr.jamailun.reignofcubes2.api.events.player.PlayerRejoinsGameEvent;
import fr.jamailun.reignofcubes2.api.events.player.PlayerScoreChangedEvent;
import fr.jamailun.reignofcubes2.api.gameplay.Throne;
import fr.jamailun.reignofcubes2.api.players.KingChangedReason;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.players.ScoreAddReason;
import fr.jamailun.reignofcubes2.api.utils.Ranking;
import fr.jamailun.reignofcubes2.configuration.GameRules;
import fr.jamailun.reignofcubes2.configuration.WorldDefinition;
import fr.jamailun.reignofcubes2.gameplay.mine.MineImpl;
import fr.jamailun.reignofcubes2.gameplay.throne.ThroneImpl;
import fr.jamailun.reignofcubes2.music.SoundsLibrary;
import fr.jamailun.reignofcubes2.players.PlayersManager;
import fr.jamailun.reignofcubes2.players.RocPlayerImpl;
import fr.jamailun.reignofcubes2.players.RocSpectator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GameStateManager implements Listener {

    // Final
    private final PlayersManager players;
    private final Ranking<RocPlayer> rankings = new Ranking<>(RocPlayer::getScore);

    // State
    private GameState state = GameState.NOT_CONFIGURED;

    // Configuration
    private RocConfiguration configuration;
    private WorldDefinition world;
    private GameRules rules;

    // Gameplay objects
    private final Set<MineImpl> mines = new HashSet<>();
    @Getter private Throne throne;
    @Getter private List<Location> spawns;
    @Getter private RocPlayer king;

    // Tasks
    private BukkitRunnable minesTask;

    public GameStateManager(@NotNull PlayersManager players) {
        this.players = players;
    }

    public @NotNull Location getRandomSpawn(boolean trySafe) {
        // Get spawns, and shuffle
        List<Location> locations = new ArrayList<>(spawns);
        Collections.shuffle(locations);

        // Iterate over spawns, check safety
        if(trySafe) {
            for(Location spawn : locations) {
                if(isSpawnSafe(spawn)) {
                    return spawn;
                }
            }
            // If none is safe... return anyway
        }

        return locations.getFirst();
    }

    private boolean isSpawnSafe(@NotNull Location location) {
        return configuration.getWorld().getNearbyPlayers(location, rules.getSpawnSafeDistance()).isEmpty();
    }

    @EventHandler
    void onConfigurationChanged(@NotNull ConfigurationChangedEvent event) throws UnknownConfigurationException {
        // Read config
        configuration = event.getNewConfig();
        world = configuration.get(WorldDefinition.NAME, WorldDefinition.class);
        rules = configuration.get(GameRules.NAME, GameRules.class);

        // Reset mines
        mines.clear();
        for(Vector vec : world.getMines()) {
            Location center = vec.toLocation(configuration.getWorld());
            MineImpl mine = new MineImpl(center, 8, 1, 100);
            mines.add(mine);
        }
    }

    @EventHandler
    void onGameStarted(@NotNull GameStartEvent ignored) {
        // Start mines production
        mines.forEach(MineImpl::reset);
        minesTask = new BukkitRunnable() {
            @Override
            public void run() {
                mines.forEach(m -> m.tick(1));
            }
        };
        minesTask.runTaskTimerAsynchronously(MainROC2.plugin(), 20, 20);

        // Initialize throne and king
        throne = new ThroneImpl(configuration.getWorld(), world.getThroneA(), world.getThroneB());
        king = null;

        // Spawns
        spawns = world.getSpawns().stream()
                .map(v -> v.toLocation(configuration.getWorld()))
                .collect(Collectors.toCollection(ArrayList::new));

        // Make player actually spawn
        for(RocPlayerImpl player : players) {
            player.teleport(getRandomSpawn(true));
            player.reset();
        }
    }

    @EventHandler
    void onGameFinished(@NotNull GameStopEvent ignored) {
        // Stop mines production
        if(minesTask != null) {
            minesTask.cancel();
            minesTask = null;
        }
        // Reset throne
        throne.resetCapture();
    }

    @EventHandler(ignoreCancelled = true)
    void onKingChanged(@NotNull KingChangedEvent event) {
        RocPlayer oldKing = event.getOldKing();
        RocPlayer newKing = event.getNewKing();

        // Always do those actions
        if(oldKing != null) {
            oldKing.setKing(false);
        }

        if(newKing != null) {
            newKing.setKing(true);
            ReignOfCubes2.broadcast("event.king.new", newKing.getName());
            newKing.addScore(rules.getScoreKingBonus(), ScoreAddReason.KING_FLAT_BONUS);
        }
        king = newKing;

        // King killed himself
        if (event.getReason().equals(KingChangedReason.OLD_KING_DIED_ALONE)) {
            if(oldKing == null) throw new RuntimeException("Cannot have " + event.getReason() + " without an old-king.");
            ReignOfCubes2.broadcast("event.king.death", oldKing.getName());
        }
    }

    @EventHandler
    void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        if(state == GameState.PLAYING) {
            // Find RocPlayer
            RocPlayer player = ReignOfCubes2.findPlayer(event.getPlayer());
            if(player == null) {
                Bukkit.getPluginManager().callEvent(new PlayerRejoinsGameEvent(new RocSpectator(event.getPlayer())));
            } else {
                Bukkit.getPluginManager().callEvent(new PlayerRejoinsGameEvent(player));
            }
            return;
        }

        // Game still not started : player joins
        RocPlayerImpl player = players.join(event.getPlayer());
        ReignOfCubes2.broadcast("event.joined", player.getName());
        players.playSound(SoundsLibrary.PLAYER_JOINED);

        // Go to lobby on join
        if(ReignOfCubes2.isLobbySet()) {
            makePlayerJoinsLobby(p);
        }

        // Test if the game should start.
        testShouldStartGame();
    }

    private synchronized void testShouldStartGame() {
        if(state != GameState.WAITING || configuration == null || ! configuration.isPlayable()) return;

        int minPlayers = rules.getPlayerCountMin();
        if(players.size() >= minPlayers) {
            ReignOfCubes2.logger().info("Enough players (" + players.size() + " >= " + minPlayers + ") ! Will start the game now.");
            state = GameState.COUNT_DOWN;
            Bukkit.getPluginManager().callEvent(new CountdownStartEvent(CountdownStartEvent.Reason.ENOUGH_PLAYERS));
        }
    }

    private synchronized void testGameShouldStopAfterPlayerLeft() {
        if(state != GameState.PLAYING) {
            ReignOfCubes2.logger().error("Unexpected call to #testGameShouldStopAfterPlayerLeft. State is " + state);
            return;
        }

        List<RocPlayer> online = players.listOnline();
        ReignOfCubes2.logger().info("A player left. Remaining online = " + online.size());
        if(online.isEmpty()) {
            ReignOfCubes2.logger().info("No players remaining. Stopping game.");
            Bukkit.getPluginManager().callEvent(new GameStopEvent(null, rankings));
        } else if(online.size() == 1) {
            RocPlayer alone = online.getFirst();
            alone.sendMessage("game.alone");
            Bukkit.getPluginManager().callEvent(new GameStopEvent(alone, rankings));
        }
    }

    @EventHandler
    void onPlayerLeave(@NotNull PlayerQuitEvent event) {
        Player p = event.getPlayer();
        players.playerLeft(p);
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

        // TODO remove player from game objects !

        // After one second, test if no/one player are remaining.
        RocScheduler.runTaskLater(this::testGameShouldStopAfterPlayerLeft, 1);
        player.setTag(null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onScoreChanged(@NotNull PlayerScoreChangedEvent event) {
        if(state != GameState.PLAYING) return;

        // update rankings
        rankings.update(event.getPlayer());

        // Check for victory.
        if(event.getPlayer().getScore() >= rules.getScoreGoal()) {
            Bukkit.getPluginManager().callEvent(new GameStopEvent(event.getPlayer(), rankings));
        }
    }

}
