package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerConnectionListener extends RocListener {
    public PlayerConnectionListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler
    void playerJoined(@NotNull PlayerJoinEvent event) {
        event.joinMessage(null);
        game().playerJoinsServer(event.getPlayer());
    }

    @EventHandler
    void playerLeft(@NotNull PlayerQuitEvent event) {
        event.quitMessage(null);
        game().playerLeftServer(event.getPlayer());
    }
}
