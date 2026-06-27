package fr.jamailun.reignofcubes2.events;

import fr.jamailun.reignofcubes2.players.RocPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when a ceremony starts.
 */
@Getter @Setter
public class CeremonyStartEvent extends Event implements Cancellable {
  private static final HandlerList HANDLERS_LIST = new HandlerList();

  private final RocPlayer player;
  private boolean cancelled = false;
  private double durationSeconds;

  public CeremonyStartEvent(@NotNull RocPlayer player, double durationSeconds) {
    this.player = player;
    this.durationSeconds = durationSeconds;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS_LIST;
  }
}
