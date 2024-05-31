package fr.jamailun.reignofcubes2.api.events.game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameStartEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public GameStartEvent() {}

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
