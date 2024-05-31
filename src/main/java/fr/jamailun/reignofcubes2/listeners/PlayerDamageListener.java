package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.events.player.RocPlayerAttacksPlayerEvent;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.Bukkit;
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
    public PlayerDamageListener(MainROC2 plugin) {
        super(plugin);
    }

    // Stores SHOOTERS protected from someone who deals them thorns damage
    private final Map<UUID, UUID> safeThorns = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void playerDamageLobby(EntityDamageEvent event) {
        if( ! game().isStatePlaying()) {
            event.setCancelled(shouldCancelNonPlaying(event));
        }
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageByEntityEvent event) {
        if( ! game().isStatePlaying()) {
            event.setCancelled(shouldCancelNonPlaying(event));
            return;
        }

        // Cancel fireworks !
        if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && event.getDamager() instanceof Firework firework) {
            if(firework.getPersistentDataContainer().has(MainROC2.marker())) {
                event.setCancelled(true);
                return;
            }
        }

        Entity victimEntity = event.getEntity();
        Entity damagerEntity = event.getDamager();

        // Victim is a player
        if(victimEntity instanceof Player pv) {
            RocPlayer victim = ReignOfCubes2.findPlayer(pv);
            if(victim == null)
                return;
            // Attacker is a Player
            if(damagerEntity instanceof Player pd) {
                RocPlayer damager = ReignOfCubes2.findPlayer(pv);
                if(damager == null) {
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
                    RocPlayer shooter = ReignOfCubes2.findPlayer(pv);
                    if(shooter == null)
                        return;

                    // We protect the shooter from potential
                    safeThorns.put(shooter.getUUID(), victim.getUUID());
                    MainROC2.runTaskLater(() -> safeThorns.remove(shooter.getUUID()), 0.3);

                    // Report attack
                    playerAttacked(shooter, victim, event);
                }
            }
        }
    }

    private void playerAttacked(RocPlayer damager, RocPlayer victim, EntityDamageByEntityEvent event) {
        victim.setLastDamager(damager);
        Bukkit.getPluginManager().callEvent(new RocPlayerAttacksPlayerEvent(damager, victim, event));
    }

    private boolean shouldCancelNonPlaying(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            boolean isVoid = e.getCause() == EntityDamageEvent.DamageCause.KILL || e.getCause() == EntityDamageEvent.DamageCause.VOID;
            return ! isVoid;
        }
        return false;
    }
}
