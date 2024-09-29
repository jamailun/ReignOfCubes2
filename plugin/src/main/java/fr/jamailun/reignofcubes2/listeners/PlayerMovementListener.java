package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.gameplay.Throne;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerMovementListener extends RocListener {

    public PlayerMovementListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    void playerMoved(@NotNull PlayerMoveEvent event) {
        if(shouldIgnore(event.getPlayer().getWorld())) {
            return;
        }

        // Too low
        Location location = event.getPlayer().getLocation();
        if(location.getY() <= gameState().get().getKillY() && event.getPlayer().getGameMode() != GameMode.SPECTATOR) {
            killVoid(event.getPlayer());
            event.setCancelled(true);
            return;
        }

        // If player is walking on BEDROCK, kill him.
        Block bottom = location.getBlock().getRelative(BlockFace.DOWN);
        if( event.getPlayer().getGameMode() != GameMode.CREATIVE
                && event.getPlayer().getGameMode() != GameMode.SPECTATOR
                && ReignOfCubes2.isPlaying()
                && bottom.getType() == Material.BEDROCK
        ) {
            killVoid(event.getPlayer());
            event.setCancelled(true);
            return;
        }

        // get RoC wrapper.
        RocPlayer player = ReignOfCubes2.findPlayer(event.getPlayer());
        if(player == null) {
            if(ReignOfCubes2.isPlaying())
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
            return;
        }

        //TODO propagate to game objects

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

    private void killVoid(@NotNull Player player) {
        player.damage(9999999);
    }
}
