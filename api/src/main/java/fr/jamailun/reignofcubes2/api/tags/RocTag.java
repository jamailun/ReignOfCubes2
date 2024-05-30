package fr.jamailun.reignofcubes2.api.tags;

import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * A tag. A player can have one tag.
 */
public interface RocTag extends Listener {

    @NotNull String getId();

    void playerAdded(@NotNull RocPlayer player);

    void playerRemoved(@NotNull RocPlayer player);

}
