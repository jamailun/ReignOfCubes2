package fr.jamailun.reignofcubes2.events;

import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.players.ScoreAddReason;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ScoreGainedEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final RocPlayer player;
    private final int delta;
    private final ScoreAddReason reason;

    public ScoreGainedEvent(RocPlayer player, int delta, ScoreAddReason reason) {
        this.player = player;
        this.delta = delta;
        this.reason = reason;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
