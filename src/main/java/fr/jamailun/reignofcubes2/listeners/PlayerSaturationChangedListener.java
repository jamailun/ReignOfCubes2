package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerSaturationChangedListener extends RocListener {
    public PlayerSaturationChangedListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void foodLevelChanged(FoodLevelChangeEvent event) {
        if( ! game().isPlaying()) {
            event.setCancelled(true);
        }
    }
}
