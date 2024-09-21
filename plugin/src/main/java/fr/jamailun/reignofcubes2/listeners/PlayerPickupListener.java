package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.jetbrains.annotations.NotNull;

@Deprecated(forRemoval = true)
public class PlayerPickupListener extends RocListener {
    public PlayerPickupListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler
    void playerPickupItem(@NotNull PlayerAttemptPickupItemEvent event) {
        if( ! game().isStatePlaying())
            return;

        RocPlayer player = ReignOfCubes2.findPlayer(event.getPlayer());
        if(player == null) {
            event.setCancelled(true);
            return;
        }

        //FIXME delete that
    }

}
