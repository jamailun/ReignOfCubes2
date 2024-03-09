package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerDamageListener extends RocListener {
    public PlayerDamageListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    private final Set<UUID> safeFireworks = new HashSet<>();
    private final static double SAFE_FW_RADIUS = 5;

    @EventHandler(priority = EventPriority.HIGH)
    public void disablePickupFireworks(FireworkExplodeEvent e) {
        Firework firework = e.getEntity();
        if(firework.getPersistentDataContainer().has(ReignOfCubes2.marker())) {
            safeFireworks.add(firework.getUniqueId());
            ReignOfCubes2.runTaskLater(() -> safeFireworks.remove(firework.getUniqueId()), 0.5);
        }
    }

    @EventHandler
    public void playerDamageLobby(EntityDamageEvent e) {
        if(!game().isPlaying()) {
            e.setCancelled(true);
        }
    }

    // For some reason, Fireworks damage trigger 'EntityDamageByBlockEvent' with a null damager ???
    @EventHandler
    public void aaa(EntityDamageByBlockEvent e) {
        if(e.getDamager() == null) {
            // Should be a firework... We MUST scan entities around
            // then find a firework in the 'safe' list.
            e.getEntity().getNearbyEntities(SAFE_FW_RADIUS, SAFE_FW_RADIUS, SAFE_FW_RADIUS)
                    .stream()
                    .map(Entity::getUniqueId)
                    .filter(safeFireworks::contains)
                    .findFirst()
                    .ifPresent(uuid -> e.setCancelled(true));
        }

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
            // Attacker is a Projectile
            else if(damagerEntity instanceof Projectile pp) {
                // get the shooter : it's a player
                if(pp.getShooter() instanceof Player pd) {
                    RocPlayer damager = game().toPlayer(pd);
                    if(playerAttacksPlayer(victim, damager)) {
                        event.setCancelled(true);
                    }
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
