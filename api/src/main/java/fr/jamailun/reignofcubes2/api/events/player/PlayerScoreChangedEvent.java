package fr.jamailun.reignofcubes2.api.events.player;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when a {@link RocPlayer} score changed.
 */
@Getter
public abstract class PlayerScoreChangedEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final RocPlayer player;
    private final int delta;

    @Setter private boolean cancelled;

    public PlayerScoreChangedEvent(@NotNull RocPlayer player, int delta) {
        this.player = player;
        this.delta = delta;
        this.cancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public int getNewScore() {
        return getPlayer().getScore() + delta;
    }

}
