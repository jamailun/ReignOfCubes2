package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener extends RocListener {
    public PlayerConnectionListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerJoined(PlayerJoinEvent event) {
        event.joinMessage(null);
        game().playerJoinsServer(event.getPlayer());
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent event) {
        event.quitMessage(null);
        game().playerLeftServer(event.getPlayer());
    }
}
