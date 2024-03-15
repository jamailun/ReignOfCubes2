package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.pickups.PickupConfigEntry;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.players.ScoreAddReason;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;

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
        spawnFirework(item.getLocation(), entry);
        // Remove item
        item.remove();
    }

    private void spawnFirework(Location location, PickupConfigEntry entry) {
        Firework fw = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(0);
        fwm.addEffect(
            FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(entry.color())
                .flicker(true)
                .trail(true)
                .withFade(entry.color().mixColors(Color.WHITE))
                .build()
        );
        fw.setFireworkMeta(fwm);
        fw.getPersistentDataContainer().set(ReignOfCubes2.marker(), PersistentDataType.BOOLEAN, true);
        fw.detonate();
    }
}
