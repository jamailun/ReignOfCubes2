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
@Getter
public class CeremonyStartEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final RocPlayer player;
    @Setter private boolean cancelled = false;

    public CeremonyStartEvent(@NotNull RocPlayer player) {
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
