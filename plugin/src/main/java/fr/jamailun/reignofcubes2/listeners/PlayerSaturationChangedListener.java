package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerSaturationChangedListener extends RocListener {
    public PlayerSaturationChangedListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void foodLevelChanged(FoodLevelChangeEvent event) {
        if( ! game().isStatePlaying()) {
            event.setCancelled(true);
        }
    }
}
