package fr.jamailun.reignofcubes2.api.tags;

import fr.jamailun.reignofcubes2.api.configuration.Persistable;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * A tag. A player can have one tag.
 */
public interface RocTag extends Listener, Persistable {

    /**
     * Get the unique ID of the tag.
     * @return a non-null string.
     */
    @NotNull String getId();

    /**
     * Call this once a place has been added.
     * @param player the player that have been added.
     */
    void playerAdded(@NotNull RocPlayer player);

    /**
     * Call this once a place has been removed.
     * @param player the player that have been removed.
     */
    void playerRemoved(@NotNull RocPlayer player);

}
