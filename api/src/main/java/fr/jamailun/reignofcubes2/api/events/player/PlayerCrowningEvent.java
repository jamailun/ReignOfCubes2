package fr.jamailun.reignofcubes2.api.events.player;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class PlayerCrowningEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @NotNull private final RocPlayer king;
    @Nullable private final RocPlayer oldKing;
    //TODO reason !

    public PlayerCrowningEvent(@NotNull RocPlayer king, @Nullable RocPlayer oldKing) {
        this.king = king;
        this.oldKing = oldKing;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
