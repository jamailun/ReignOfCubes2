package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.players.PlayersManager;
import org.bukkit.event.Listener;

public class RocListener implements Listener {

    private final ReignOfCubes2 plugin;

    public RocListener(ReignOfCubes2 plugin) {
        this.plugin = plugin;
    }

    protected ReignOfCubes2 plugin() {
        return plugin;
    }

    protected GameManager game() {
        return plugin.getGameManager();
    }

}
