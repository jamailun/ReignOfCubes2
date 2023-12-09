package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener extends RocListener {
    public PlayerConnectionListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerJoined(PlayerJoinEvent event) {
        game().playerJoinsServer(event.getPlayer());
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent event) {
        game().playerLeftServer(event.getPlayer());
    }
}
