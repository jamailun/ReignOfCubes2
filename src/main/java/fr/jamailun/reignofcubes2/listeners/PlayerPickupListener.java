package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.configuration.pickups.PickupConfigEntry;
import fr.jamailun.reignofcubes2.players.RocPlayerImpl;
import fr.jamailun.reignofcubes2.players.ScoreAddReason;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

public class PlayerPickupListener extends RocListener {
    public PlayerPickupListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerPickupItem(PlayerAttemptPickupItemEvent event) {
        if( ! game().isStatePlaying())
            return;

        RocPlayerImpl player = game().toPlayer(event.getPlayer());
        if(player == null) {
            event.setCancelled(true);
            return;
        }

        Item item = event.getItem();
        game().didPickedUpItem(item).ifPresent(entry -> {
            event.setCancelled(true);
            pickupItem(player, item, entry);
        });
    }

    private void pickupItem(RocPlayerImpl player, Item item, PickupConfigEntry entry) {
        // Score
        player.addScore(entry.score(), ScoreAddReason.PICKUP);
        MainROC2.updateRanks(player);
        // Effect
        entry.spawnFirework(item.getLocation());
        // Remove item
        item.remove();
    }


}
