package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.players.RocPlayer;
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

        // Change the respawn location.
        event.setRespawnLocation(game().getWorldConfiguration().getSafeSpawn(true));

        // Signal respawn to the player class.
        RocPlayer player = game().toPlayer(event.getPlayer());
        if(player != null) {
            player.respawned();
        }
    }

}
