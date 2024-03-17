package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A tag for a ninja.
 * <br/>
 * A ninja can sneak to become COMPLETELY INVISIBLE.
 */
public class NinjaTag extends Tag {

    public NinjaTag() {
        super("ninja");
    }

    private final Set<UUID> hiddenPlayers = new HashSet<>();

    @Override
    public void added(@NotNull RocPlayer holder) {
        ReignOfCubes2.info("[debug] " + holder.getName() + " is now NINJA");
        holder.getPlayer().showDemoScreen();
    }

    @Override
    public void removed(@NotNull RocPlayer holder) {
        ReignOfCubes2.info("[debug] " + holder.getName() + " is not NINJA anymore.");
        show(holder);
    }

    public void hide(RocPlayer rocPlayer) {
        if(hiddenPlayers.contains(rocPlayer.getUUID()))
            return;
        hiddenPlayers.add(rocPlayer.getUUID());

        Player player = rocPlayer.getPlayer();
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 9, true, true, true));
        player.getWorld().getPlayers().forEach(p -> {
            if(!(p.equals(player))) {
                p.hidePlayer(ReignOfCubes2.plugin(), player);
            }
        });
        player.getWorld().spawnParticle(Particle.DRIP_LAVA, player.getLocation(), 10);
    }

    public void show(RocPlayer rocPlayer) {
        if(!hiddenPlayers.contains(rocPlayer.getUUID()))
            return;;
        hiddenPlayers.remove(rocPlayer.getUUID());

        Player player = rocPlayer.getPlayer();
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.getWorld().getPlayers().forEach(p -> {
            if(!(p.equals(player))) {
                p.showPlayer(ReignOfCubes2.plugin(), player);
            }
        });

        player.getWorld().spawnParticle(Particle.DRIP_LAVA, player.getLocation(), 10);
    }

    @Override
    public void holderAttacks(@NotNull RocPlayer holder, @NotNull RocPlayer other, @NotNull EntityDamageByEntityEvent event) {
       show(holder);
    }

    @Override
    public void holderDefends(@NotNull RocPlayer holder, @Nullable RocPlayer other, @NotNull EntityDamageEvent event) {
        show(holder);
    }
}
