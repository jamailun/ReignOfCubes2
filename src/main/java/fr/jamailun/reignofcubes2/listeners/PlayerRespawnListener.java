package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.players.RocPlayerImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener extends RocListener {

    public PlayerRespawnListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        if(shouldIgnore(event.getPlayer().getWorld())) {
            return;
        }
        RocPlayerImpl player = game().getPlayerImplementation(event.getPlayer());
        if(player == null)
            return; // Ignore completely

        // Change the respawn location.
        event.setRespawnLocation(game().getActiveConfiguration().getSafeSpawn(true));

        // Signal respawn to the player class.
        player.respawned();
    }

}
