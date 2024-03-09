package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.players.ScoreAddReason;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

public class PlayerPickupListener extends RocListener {
    public PlayerPickupListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerPickupItem(PlayerAttemptPickupItemEvent event) {
        if( ! game().isPlaying())
            return;

        RocPlayer player = game().toPlayer(event.getPlayer());
        if(player == null) {
            event.setCancelled(true);
            return;
        }

        Item item = event.getItem();
        if(game().didPickedUpItem(item)) {
            player.addScore(game().getRules().getScorePickup(), ScoreAddReason.PICKUP);
            event.setCancelled(true);
            item.remove();
            //TODO firework & sound
        }
    }
}
