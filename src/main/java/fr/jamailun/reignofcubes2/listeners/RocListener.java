package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.GameManagerImpl;
import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;

abstract class RocListener implements Listener {

    private final MainROC2 plugin;

    public RocListener(MainROC2 plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        ReignOfCubes2.logInfo("Registering events on " + getClass().getSimpleName());
    }

    protected MainROC2 plugin() {
        return plugin;
    }

    protected GameManagerImpl game() {
        return plugin.getGameManager();
    }

    protected boolean shouldIgnore(World world) {
        return ! game().isStatePlaying()
                || ! game().isInWorld(world);
    }

}
