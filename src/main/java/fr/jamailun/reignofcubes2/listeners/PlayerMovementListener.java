package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.Throne;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener extends RocListener {

    public PlayerMovementListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerMoved(PlayerMoveEvent event) {
        if(!(game().isInWorld(event.getPlayer().getWorld()) && game().isPlaying())) {
            return;
        }

        RocPlayer player = game().toPlayer(event.getPlayer());
        if(player == null) return;

        // Parameters
        Throne throne = game().getThrone();
        boolean inside = throne.isInside(event.getTo());

        // Is already inside ?
        if(throne.isAlreadyInside(player)) {
            if(!inside)
                throne.leaves(player);
        }

        // Is now inside ?
        else {
            if(inside)
                throne.enters(player);
        }

    }
}
