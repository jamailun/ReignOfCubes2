package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.objects.Throne;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener extends RocListener {

    public PlayerMovementListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerMoved(PlayerMoveEvent event) {
        if(shouldIgnore(event.getPlayer().getWorld())) {
            return;
        }

        // If player is walking on BEDROCK, kill him.
        Block bottom = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
        if( event.getPlayer().getGameMode() != GameMode.CREATIVE
                && event.getPlayer().getGameMode() != GameMode.SPECTATOR
                && game().isPlaying()
                && bottom.getType() == Material.BEDROCK
        ) {
            event.getPlayer().damage(9999999);
            return;
        }

        // get RoC wrapper.
        RocPlayer player = game().toPlayer(event.getPlayer());
        if(player == null) return;

        // Test if is inside the throne.
        Throne throne = game().getThrone();
        boolean inside = throne.isInside(event.getTo());

        // Is already inside ?
        if(throne.isAlreadyInside(player)) {
            if(!inside)
                throne.leaves(player);
        }

        // Is now inside ?
        else {
            if(inside && throne.isCooldownOk(player.getUUID()))
                throne.enters(player);
        }

    }
}
