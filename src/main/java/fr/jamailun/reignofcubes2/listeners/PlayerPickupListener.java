package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.pickups.PickupConfigEntry;
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
        game().didPickedUpItem(item).ifPresent(entry -> {
            event.setCancelled(true);
            pickupItem(player, item, entry);
        });
    }

    private void pickupItem(RocPlayer player, Item item, PickupConfigEntry entry) {
        // Score
        player.addScore(entry.score(), ScoreAddReason.PICKUP);
        ReignOfCubes2.updateRanks(player);
        // Effect
        entry.spawnFirework(item.getLocation());
        // Remove item
        item.remove();
    }


}
