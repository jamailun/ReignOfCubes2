package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.Nullable;

public class PlayerDamageListener extends RocListener {
    public PlayerDamageListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageByEntityEvent event) {
        Entity victimEntity = event.getEntity();
        Entity damagerEntity = event.getDamager();
        // Victim is a player
        if(victimEntity instanceof Player pv) {
            RocPlayer victim = game().toPlayer(pv);
            if(victim == null)
                return;
            // Attacker is a Player
            if(damagerEntity instanceof Player pd) {
                RocPlayer damager = game().toPlayer(pd);
                if(playerAttacksPlayer(victim, damager)) {
                    event.setCancelled(true);
                }
            }
            //TODO PVE - last damager
        }
    }

    /**
     * A player attacks another.
     * @param victim the victim.
     * @param damager the damager. Can be null.
     * @return true if the venet should be cancelled.
     */
    private boolean playerAttacksPlayer(RocPlayer victim, @Nullable RocPlayer damager) {
        // If the game hasn't started, cancel
        if( ! game().isPlaying()) {
            return true;
        }

        if(damager == null)
            return false;
        victim.setLastDamager(damager);
        return false;
    }
}
