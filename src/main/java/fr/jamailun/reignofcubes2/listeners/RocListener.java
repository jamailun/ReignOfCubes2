package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.players.PlayersManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;

class RocListener implements Listener {

    private final ReignOfCubes2 plugin;

    public RocListener(ReignOfCubes2 plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        ReignOfCubes2.info("Registering events on " + getClass().getSimpleName());
    }

    protected ReignOfCubes2 plugin() {
        return plugin;
    }

    protected GameManager game() {
        return plugin.getGameManager();
    }

    protected boolean shouldIgnore(World world) {
        return ! game().isPlaying()
                || ! game().isInWorld(world);
    }

}
