package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.events.RocPlayerAttacksPlayerEvent;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A tag for a ninja.
 * <br/>
 * A ninja can sneak to become COMPLETELY INVISIBLE.
 */
public class NinjaTag extends AbstractRocTag {

    public NinjaTag() {
        super("ninja");
    }

    //TODO configurable
    private static final double COOLDOWN = 5000;

    private final Set<UUID> hiddenPlayers = new HashSet<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void playerAdded(@NotNull RocPlayer holder) {
        MainROC2.info("[debug] " + holder.getName() + " is now NINJA");
        holder.getPlayer().showDemoScreen();
    }

    @Override
    public void playerRemoved(@NotNull RocPlayer holder) {
        MainROC2.info("[debug] " + holder.getName() + " is not NINJA anymore.");
        show(holder);
    }

    public void hide(RocPlayer rocPlayer) {
        UUID uuid = rocPlayer.getUUID();
        if(hiddenPlayers.contains(uuid))
            return;
        if(cooldowns.containsKey(uuid)) {
            long last = cooldowns.get(uuid);
            long delta = System.currentTimeMillis() - last;
            if(delta <= COOLDOWN) {
                rocPlayer.sendMessage("tags.ninja.cooldown");
                return;
            }
        }
        hiddenPlayers.add(uuid);
        rocPlayer.sendMessage("tags.ninja.hidden");

        Player player = rocPlayer.getPlayer();
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 9, true, true, true));
        player.getWorld().getPlayers().forEach(p -> {
            if(!(p.equals(player))) {
                p.hidePlayer(MainROC2.plugin(), player);
            }
        });
        player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getLocation().clone().add(0, 0.6, 0), 2);
    }

    public void show(RocPlayer rocPlayer) {
        if(!hiddenPlayers.contains(rocPlayer.getUUID()))
            return;
        hiddenPlayers.remove(rocPlayer.getUUID());
        cooldowns.put(rocPlayer.getUUID(), System.currentTimeMillis());
        rocPlayer.sendMessage("tags.ninja.visible");

        Player player = rocPlayer.getPlayer();
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.getWorld().getPlayers().forEach(p -> {
            if(!(p.equals(player))) {
                p.showPlayer(MainROC2.plugin(), player);
            }
        });

        player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getLocation().clone().add(0, 0.6, 0), 2);
    }


    @EventHandler
    public void attackEvent(RocPlayerAttacksPlayerEvent event) {
        if(event.getAttacker().isTag(this)) {
            show(event.getAttacker());
            event.getBukkitEvent().setDamage(event.getBukkitEvent().getDamage() / 2);
        }

        if(event.getVictim().isTag(this)) {
            show(event.getVictim());
        }
    }


    @EventHandler
    public void playerSneak(PlayerToggleSneakEvent event) {
        RocPlayer player = ReignOfCubes2.findPlayer(event.getPlayer());
        if(player != null && player.isTag(this)) {
            if(event.getPlayer().isSneaking()) {
                show(player);
            } else {
                hide(player);
            }
        }
    }

}
