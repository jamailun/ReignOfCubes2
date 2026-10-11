package fr.jamailun.reignofcubes2.events;

import fr.jamailun.reignofcubes2.players.RocPlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class KingChangedEvent extends Event {
  private static final HandlerList HANDLERS_LIST = new HandlerList();

  private final @Nullable RocPlayer newKing;
  private final @Nullable RocPlayer oldKing;

  public KingChangedEvent(@Nullable RocPlayer oldKing, @Nullable RocPlayer newKing) {
    this.newKing = newKing;
    this.oldKing = oldKing;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS_LIST;
  }
}
