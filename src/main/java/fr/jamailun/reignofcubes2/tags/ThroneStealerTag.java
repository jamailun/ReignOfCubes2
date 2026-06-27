package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.events.CeremonyStartEvent;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A tag for a throne stealer : dive
 */
public class ThroneStealerTag extends Tag implements Listener {

  public ThroneStealerTag(@NotNull ReignOfCubes2 plugin) {
    super("throne_stealer", plugin);
  }

  @Override
  public void added(@NotNull RocPlayer holder) {
    ReignOfCubes2.debug(holder.getName() + " is now THRONE_STEALER.");
  }

  @Override
  public void removed(@NotNull RocPlayer holder) {
    ReignOfCubes2.debug(holder.getName() + " is not THRONE_STEALER anymore.");
  }

  @Override
  public void holderAttacks(@NotNull RocPlayer holder, @NotNull RocPlayer other, @NotNull EntityDamageByEntityEvent event) {
   // nothing
  }

  @Override
  public void holderDefends(@NotNull RocPlayer holder, @Nullable RocPlayer other, @NotNull EntityDamageEvent event) {
    // nothing
  }

  @EventHandler
  void playerStartsCapturing(@NotNull CeremonyStartEvent event) {
    if(is(event.getPlayer())) {
      boolean stealing = plugin.getGameManager().hasKing();
      double multiplier = stealing ? ReignOfCubes2.getTags().getThroneStealerModifierStealing() : ReignOfCubes2.getTags().getThroneStealerModifierNormal();
      double originalDuration = event.getDurationSeconds();
      event.setDurationSeconds(originalDuration * multiplier);
      ReignOfCubes2.info("Throne changed capture time from " + originalDuration + " to " + event.getDurationSeconds() + ".");
    }
  }
}
