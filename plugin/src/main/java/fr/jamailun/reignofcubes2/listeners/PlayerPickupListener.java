package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.players.ScoreAddReason;
import fr.jamailun.reignofcubes2.configuration.pickups.PickupConfigEntry;
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

        RocPlayer player = ReignOfCubes2.findPlayer(event.getPlayer());
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
       //TODO REMOVE player.addScore(entry.score(), ScoreAddReason.PICKUP);
        ReignOfCubes2.gameManager().updateRankings(player);
        // Effect
        entry.spawnFirework(item.getLocation());
        // Remove item
        item.remove();
    }

}
