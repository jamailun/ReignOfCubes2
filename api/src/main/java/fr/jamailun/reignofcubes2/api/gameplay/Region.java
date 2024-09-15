package fr.jamailun.reignofcubes2.api.gameplay;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A region of a World.
 */
public interface Region {

    /**
     * Get the center of the region.
     * @return a non-null bukkit location.
     */
    @NotNull Location getCenter();

    /**
     * Get all the players inside the object (or its zone of influence).
     * @return a non-null, non-mutable set of players.
     */
    @NotNull Set<RocPlayer> playersInside();

    /**
     * Test if a player is inside the zone.
     * @param player a non-null player.
     * @return true if the player is the zone.
     */
    default boolean isAlreadyInside(@NotNull RocPlayer player) {
        return playersInside().contains(player);
    }

    /**
     * Signal the throne a player entered it.
     * @param player the player to enter.
     */
    void enters(@NotNull RocPlayer player);

    /**
     * Signal the throne a player left it.
     * @param player the player to leave.
     */
    void leaves(@NotNull RocPlayer player);

    boolean isInside(@NotNull Location location);

}
