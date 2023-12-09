package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovement extends RocListener {

    public PlayerMovement(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerMoved(PlayerMoveEvent event) {
        if(!game().isInWorld(event.getPlayer().getWorld())) {
            return;
        }

    }
}
