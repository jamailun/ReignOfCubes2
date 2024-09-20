package fr.jamailun.reignofcubes2.api.events.player;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.players.ScoreRemoveReason;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when a {@link RocPlayer} score went DOWN.
 */
@Getter
public class PlayerScoreLostEvent extends PlayerScoreChangedEvent {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final ScoreRemoveReason reason;

    public PlayerScoreLostEvent(@NotNull RocPlayer player, int delta, @NotNull ScoreRemoveReason reason) {
        super(player, delta);
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
