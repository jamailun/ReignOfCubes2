package fr.jamailun.reignofcubes2.api.events.player;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class PlayerRejoinsGameEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @NotNull private final RocPlayer player;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
