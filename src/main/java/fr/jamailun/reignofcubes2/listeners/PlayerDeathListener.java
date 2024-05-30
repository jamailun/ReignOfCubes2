package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener extends RocListener {
    public PlayerDeathListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerKilledEvent(PlayerDeathEvent event) {
        Player v = event.getPlayer();
        RocPlayer victim = ReignOfCubes2.findPlayer(v);
        if(victim == null || shouldIgnore(v.getWorld())) {
            return;
        }
        game().playerDies(victim);
        event.deathMessage(null);
    }

    @EventHandler
    public void savePunchingBallInLobby(EntityDeathEvent event) {
        if(game().isStatePlaying())
            return;
        if(!(event.getEntity() instanceof Player)) {
            EntityDamageEvent last = event.getEntity().getLastDamageCause();
            if(last == null) {
                event.setCancelled(false);
                return;
            }
            boolean isVoid = last.getCause() == EntityDamageEvent.DamageCause.KILL || last.getCause() == EntityDamageEvent.DamageCause.VOID;
            if(!isVoid) {
                event.setCancelled(true);
                event.setShouldPlayDeathSound(false);
                event.setReviveHealth(20);
            }
        }
    }
}
