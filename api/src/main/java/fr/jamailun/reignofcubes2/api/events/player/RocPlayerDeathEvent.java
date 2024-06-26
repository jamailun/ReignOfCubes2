package fr.jamailun.reignofcubes2.api.events.player;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class RocPlayerDeathEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @NotNull private final RocPlayer player;

    public RocPlayerDeathEvent(@NotNull RocPlayer player) {
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
