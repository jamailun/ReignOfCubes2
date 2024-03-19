package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

public class PlayerDamageListener extends RocListener {
    public PlayerDamageListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    // Stores SHOOTERS protected from someone who deals them thorns damage
    private final Map<UUID, UUID> safeThorns = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void playerDamageLobby(EntityDamageEvent event) {
        if( ! game().isPlaying()) {
            event.setCancelled(shouldCancelNonPlaying(event));
        }
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageByEntityEvent event) {
        if( ! game().isPlaying()) {
            event.setCancelled(shouldCancelNonPlaying(event));
            return;
        }

        // Cancel fireworks !
        if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && event.getDamager() instanceof Firework firework) {
            if(firework.getPersistentDataContainer().has(ReignOfCubes2.marker())) {
                event.setCancelled(true);
                return;
            }
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
                if(damager == null) {
                    victim.getTag().ifPresent(tag -> tag.holderDefends(victim, null, event));
                    return;
                }

                // Protect from thorns damage IF from projectile-revenge
                if(event.getCause() == EntityDamageEvent.DamageCause.THORNS) {
                    if(damager.getUUID().equals(safeThorns.get(victim.getUUID()))) {
                        event.setCancelled(true);
                        return;
                    }
                }

                playerAttacked(damager, victim, event);
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

                    // Report attack
                    playerAttacked(shooter, victim, event);
                }
            }
        }
    }

    private void playerAttacked(RocPlayer damager, RocPlayer victim, EntityDamageByEntityEvent event) {
        damager.getTag().ifPresent(t -> t.holderAttacks(damager, victim, event));
        victim.getTag().ifPresent(t -> t.holderDefends(damager, victim, event));
        victim.setLastDamager(damager);
    }

    private boolean shouldCancelNonPlaying(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            boolean isVoid = e.getCause() == EntityDamageEvent.DamageCause.KILL || e.getCause() == EntityDamageEvent.DamageCause.VOID;
            return ! isVoid;
        }
        return false;
    }
}
