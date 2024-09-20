package fr.jamailun.reignofcubes2.api.events.game;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.utils.Ranking;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class GameStopEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Nullable private final RocPlayer winner;
    @NotNull private final Ranking<RocPlayer> ranking;

    public GameStopEvent(@Nullable RocPlayer winner, @NotNull Ranking<RocPlayer> ranking) {
        this.winner = winner;
        this.ranking = ranking;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
