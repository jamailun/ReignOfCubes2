package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.GameState;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.state.GameStateManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

abstract class RocListener implements Listener {

    private final GameStateManager gameState;

    public RocListener(@NotNull MainROC2 plugin) {
        this.gameState = plugin.getGameState();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        ReignOfCubes2.logger().info("Registering events on " + getClass().getSimpleName());
    }

    protected GameStateManager gameState() {
        return gameState;
    }

    protected boolean shouldIgnore(@NotNull World world) {
        return ReignOfCubes2.state() != GameState.PLAYING
                || ReignOfCubes2.isInWorld(world);
    }

}
