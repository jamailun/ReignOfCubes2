package fr.jamailun.reignofcubes2.api.events.player;

import fr.jamailun.reignofcubes2.api.players.KingChangedReason;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when the King changed.
 */
@Getter
public class KingChangedEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Nullable private final RocPlayer newKing;
    @Nullable private final RocPlayer oldKing;
    @NotNull private final KingChangedReason reason;

    public KingChangedEvent(@Nullable RocPlayer newKing, @Nullable RocPlayer oldKing, @NotNull KingChangedReason reason) {
        this.newKing = newKing;
        this.oldKing = oldKing;
        this.reason = reason;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
