package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.players.RocPlayerImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerRespawnListener extends RocListener {

    public PlayerRespawnListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler
    void playerRespawn(@NotNull PlayerRespawnEvent event) {
        if(shouldIgnore(event.getPlayer().getWorld())) {
            return;
        }
        RocPlayer player = ReignOfCubes2.findPlayer(event.getPlayer());
        if(player == null)
            return; // Ignore completely

        // Change the respawn location.
        event.setRespawnLocation(game().getActiveConfiguration().getSafeSpawn(true));

        // Signal respawn to the player class.
        player.reset();
    }

}
