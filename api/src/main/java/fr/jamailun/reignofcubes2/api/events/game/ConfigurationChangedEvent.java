package fr.jamailun.reignofcubes2.api.events.game;

import fr.jamailun.reignofcubes2.api.configuration.RocConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
public class ConfigurationChangedEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Nullable
    private final RocConfiguration oldConfig;
    @NotNull private final RocConfiguration newConfig;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
