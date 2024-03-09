package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
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
        if(game().didPickedUpItem(item)) {
            // Score
            player.addScore(game().getRules().getScorePickup(), ScoreAddReason.PICKUP);
            ReignOfCubes2.updateRanks(player);
            // Effect
            spawnFirework(item.getLocation());
            // Remove item
            event.setCancelled(true);
            item.remove();
        }
    }

    private void spawnFirework(Location location) {
        Firework fw = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(0);
        fwm.addEffect(
            FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.GREEN, Color.OLIVE, Color.YELLOW)
                .flicker(true)
                .trail(true)
                .withFade(Color.LIME)
                .build()
        );
        fw.setFireworkMeta(fwm);
        fw.getPersistentDataContainer().set(ReignOfCubes2.marker(), PersistentDataType.BOOLEAN, true);
        fw.detonate();
    }
}
