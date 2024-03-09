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

import java.util.*;

public class PlayerDamageListener extends RocListener {
    public PlayerDamageListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    // Stores UUID of firework that shall not deal damages
    private final Set<UUID> safeFireworks = new HashSet<>();
    // Stores SHOOTERS protected from someone who deals them thorns damage
    private final Map<UUID, UUID> safeThorns = new HashMap<>();
    // Because firework damages are dealt poorly in the API, must look up for firweorks manually...
    private final static double SAFE_FW_RADIUS = 5;

    @EventHandler(priority = EventPriority.HIGH)
    public void disablePickupFireworks(FireworkExplodeEvent e) {
        Firework firework = e.getEntity();
        if(firework.getPersistentDataContainer().has(ReignOfCubes2.marker())) {
            safeFireworks.add(firework.getUniqueId());
            ReignOfCubes2.runTaskLater(() -> safeFireworks.remove(firework.getUniqueId()), 0.5);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerDamageLobby(EntityDamageEvent e) {
        if(!game().isPlaying()) {
            e.setCancelled(true);
        }
    }

    // For some reason, Fireworks damage trigger 'EntityDamageByBlockEvent' with a null damager ???
    @EventHandler
    public void cancelFireworkDamages(EntityDamageByBlockEvent e) {
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
        if( ! game().isPlaying()) {
            event.setCancelled(true);
            return;
        }

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
                if(damager == null)
                    return;

                // Protect from thorns damage IF from projectile-revenge
                if(event.getCause() == EntityDamageEvent.DamageCause.THORNS) {
                    if(damager.getUUID().equals(safeThorns.get(victim.getUUID()))) {
                        event.setCancelled(true);
                        return;
                    }
                }

                victim.setLastDamager(damager);
            }
            // Attacker is a Projectile
            else if(damagerEntity instanceof Projectile pp) {
                // get the shooter : it's a player
                if(pp.getShooter() instanceof Player pd) {
                    RocPlayer shooter = game().toPlayer(pd);
                    if(shooter == null)
                        return;

                    // We protect the shooter from potential
                    safeThorns.put(shooter.getUUID(), victim.getUUID());
                    ReignOfCubes2.runTaskLater(() -> safeThorns.remove(shooter.getUUID()), 0.3);

                    victim.setLastDamager(shooter);
                }
            }
        }
    }
}
