package fr.jamailun.reignofcubes2.api.events.player;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.players.ScoreAddReason;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ScoreGainedEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final RocPlayer player;
    private final int delta;
    private final ScoreAddReason reason;
    @Setter private boolean cancelled = false;

    public ScoreGainedEvent(RocPlayer player, int delta, ScoreAddReason reason) {
        this.player = player;
        this.delta = delta;
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
