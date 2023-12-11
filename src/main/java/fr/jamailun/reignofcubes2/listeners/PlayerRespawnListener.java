package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener extends RocListener {

    public PlayerRespawnListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        if(shouldIgnore(event.getPlayer().getWorld())) {
            return;
        }
        event.setRespawnLocation(game().getWorldConfiguration().getSafeSpawn(true));
    }

}
