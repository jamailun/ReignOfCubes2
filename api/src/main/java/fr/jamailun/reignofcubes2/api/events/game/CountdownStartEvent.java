package fr.jamailun.reignofcubes2.api.events.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class CountdownStartEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Getter @NotNull Reason reason;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public enum Reason {
        ADMINISTRATOR,
        ENOUGH_PLAYERS
    }
}
