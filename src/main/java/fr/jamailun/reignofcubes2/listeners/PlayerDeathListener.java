package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener extends RocListener {
    public PlayerDeathListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageByEntityEvent event) {
        Entity victimEntity = event.getEntity();
        Entity damagerEntity = event.getDamager();
        if(victimEntity instanceof Player pv) {
            RocPlayer victim = game().toPlayer(pv);
            if(victim == null)
                return;
            if(damagerEntity instanceof Player pd) {
                RocPlayer damager = game().toPlayer(pd);
                if(damager == null)
                    return;
                victim.setLastDamager(damager);
            }
            //TODO PVE - last damager
        }
    }

    @EventHandler
    public void playerKilledEvent(PlayerDeathEvent event) {
        Player v = event.getPlayer();
        RocPlayer victim = game().toPlayer(v);
        if(victim == null || shouldIgnore(v.getWorld())) {
            return;
        }
        game().playerDies(victim);
        event.deathMessage(null);
    }
}
