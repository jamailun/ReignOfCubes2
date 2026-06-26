package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.events.CeremonyStartEvent;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A tag for a ninja.
 * <br/>
 * A ninja can sneak to become COMPLETELY INVISIBLE.
 */
public class NinjaTag extends Tag implements Listener {

  public NinjaTag(@NotNull ReignOfCubes2 plugin) {
    super("ninja", plugin);
  }

  private final Set<UUID> hiddenPlayers = new HashSet<>();
  private final Map<UUID, Long> cooldowns = new HashMap<>();

  @Override
  public void added(@NotNull RocPlayer holder) {
    ReignOfCubes2.debug(holder.getName() + " is now NINJA");
    holder.getPlayer().showDemoScreen();
  }

  @Override
  public void removed(@NotNull RocPlayer holder) {
    ReignOfCubes2.debug(holder.getName() + " is not NINJA anymore.");
    show(holder);
  }

  public void hide(RocPlayer rocPlayer) {
    int cooldown = ReignOfCubes2.getTags().getNinjaCooldownMs();

    UUID uuid = rocPlayer.getUUID();
    if (hiddenPlayers.contains(uuid))
      return;
    if (cooldowns.containsKey(uuid)) {
      long last = cooldowns.get(uuid);
      long delta = System.currentTimeMillis() - last;
      if (delta <= cooldown) {
        rocPlayer.sendMessage("tags.ninja.cooldown");
        return;
      }
    }
    hiddenPlayers.add(uuid);
    rocPlayer.sendMessage("tags.ninja.hidden");

    Player player = rocPlayer.getPlayer();
    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 9, true, true, true));
    player.getWorld().getPlayers().forEach(p -> {
      if (!(p.equals(player))) {
        p.hidePlayer(ReignOfCubes2.plugin(), player);
      }
    });
    player.getWorld().spawnParticle(Particle.LARGE_SMOKE, player.getLocation().clone().add(0, 0.6, 0), 2);
  }

  public void show(RocPlayer rocPlayer) {
    if (!hiddenPlayers.contains(rocPlayer.getUUID()))
      return;
    hiddenPlayers.remove(rocPlayer.getUUID());
    cooldowns.put(rocPlayer.getUUID(), System.currentTimeMillis());
    rocPlayer.sendMessage("tags.ninja.visible");

    Player player = rocPlayer.getPlayer();
    player.removePotionEffect(PotionEffectType.INVISIBILITY);
    player.getWorld().getPlayers().forEach(p -> {
      if (!(p.equals(player))) {
        p.showPlayer(ReignOfCubes2.plugin(), player);
      }
    });

    player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().clone().add(0, 0.6, 0), 30, 3, 3, 0.3, 0.01);
  }

  @Override
  public void holderAttacks(@NotNull RocPlayer holder, @NotNull RocPlayer other, @NotNull EntityDamageByEntityEvent event) {
    boolean isInvisible = hiddenPlayers.contains(holder.getUUID());
    show(holder);

    // Reduce damage when exiting stealth
    if (isInvisible)
      event.setDamage(event.getDamage() / 2);
  }

  @Override
  public void holderDefends(@NotNull RocPlayer holder, @Nullable RocPlayer other, @NotNull EntityDamageEvent event) {
    show(holder);
  }

  @EventHandler
  void playerSneak(@NotNull PlayerToggleSneakEvent event) {
    Player player = event.getPlayer();
    RocPlayer hPlayer = getRocPlayer(player);
    if(hPlayer != null && is(hPlayer)) {
      if (player.isSneaking()) {
        show(hPlayer);
      } else {
        hide(hPlayer);
      }
    }
  }

  @EventHandler
  void playerStartsCapturing(@NotNull CeremonyStartEvent event) {
    if(is(event.getPlayer()) && ReignOfCubes2.getTags().isNinjaCaptureDisableStealth()) {
      show(event.getPlayer());
    }
  }
}
