package fr.jamailun.reignofcubes2.gameplay;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.configuration.RocConfiguration;
import fr.jamailun.reignofcubes2.api.configuration.UnknownConfigurationException;
import fr.jamailun.reignofcubes2.api.events.game.ConfigurationChangedEvent;
import fr.jamailun.reignofcubes2.api.events.game.GameStartEvent;
import fr.jamailun.reignofcubes2.api.events.game.GameStopEvent;
import fr.jamailun.reignofcubes2.api.gameplay.Throne;
import fr.jamailun.reignofcubes2.configuration.GameRules;
import fr.jamailun.reignofcubes2.configuration.WorldDefinition;
import fr.jamailun.reignofcubes2.gameplay.mine.MineImpl;
import fr.jamailun.reignofcubes2.gameplay.throne.ThroneImpl;
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

public class GameStateHandler implements Listener {

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

    // Tasks
    private BukkitRunnable minesTask;

    public GameStateHandler(@NotNull Supplier<Location> defaultLobbySupplier) {
        this.defaultLobbySupplier = defaultLobbySupplier;
    }

    @EventHandler
    void configurationChanged(@NotNull ConfigurationChangedEvent event) throws UnknownConfigurationException {
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
    void gameStarted(@NotNull GameStartEvent ignored) {
        // Start mines production
        mines.forEach(MineImpl::reset);
        minesTask = new BukkitRunnable() {
            @Override
            public void run() {
                mines.forEach(m -> m.tick(1));
            }
        };
        minesTask.runTaskTimerAsynchronously(MainROC2.plugin(), 20, 20);

        // Initialize throne
        throne = new ThroneImpl(configuration.getWorld(), world.getThroneA(), world.getThroneB());

        // Spawns
        spawns = world.getSpawns().stream()
                .map(v -> v.toLocation(configuration.getWorld()))
                .collect(Collectors.toCollection(ArrayList::new));
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
    void gameFinished(@NotNull GameStopEvent ignored) {
        // Stop mines production
        if(minesTask != null) {
            minesTask.cancel();
            minesTask = null;
        }

    }

}
