package fr.jamailun.reignofcubes2.state;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.configuration.RocConfiguration;
import fr.jamailun.reignofcubes2.api.configuration.UnknownConfigurationException;
import fr.jamailun.reignofcubes2.api.events.game.ConfigurationChangedEvent;
import fr.jamailun.reignofcubes2.api.events.game.GameStartEvent;
import fr.jamailun.reignofcubes2.api.events.game.GameStopEvent;
import fr.jamailun.reignofcubes2.api.events.player.KingChangedEvent;
import fr.jamailun.reignofcubes2.api.gameplay.Throne;
import fr.jamailun.reignofcubes2.api.music.MusicType;
import fr.jamailun.reignofcubes2.api.players.KingChangedReason;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.players.ScoreAddReason;
import fr.jamailun.reignofcubes2.configuration.GameRules;
import fr.jamailun.reignofcubes2.configuration.WorldDefinition;
import fr.jamailun.reignofcubes2.gameplay.mine.MineImpl;
import fr.jamailun.reignofcubes2.gameplay.throne.ThroneImpl;
import fr.jamailun.reignofcubes2.music.SoundsLibrary;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GameStateManager implements Listener {

    // Default
    private final Supplier<Location> defaultLobbySupplier;

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

    public GameStateManager(@NotNull Supplier<Location> defaultLobbySupplier) {
        this.defaultLobbySupplier = defaultLobbySupplier;
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
            broadcast("event.king.new", newKing.getName());
            newKing.addScore(rules.getScoreKingBonus(), ScoreAddReason.KING_FLAT_BONUS);
        }
        king = newKing;

        // King killed himself
        if (event.getReason().equals(KingChangedReason.OLD_KING_DIED_ALONE)) {
            if(oldKing == null) throw new RuntimeException("Cannot have " + event.getReason() + " without an old-king.");
            broadcast("event.king.death", oldKing.getName());
        }
    }

}
