package fr.jamailun.reignofcubes2.api.gameplay;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * A ceremony is a {@link Throne} capture by a {@link RocPlayer}.
 */
public interface Ceremony {

    /**
     * Force the ceremony to stop.
     */
    void stop();

    /**
     * Check the owner of the ceremony.
     * @param player a player to check.
     * @return true if the argument player is the one of the Ceremony.
     */
    boolean isPlayer(@NotNull RocPlayer player);

    /**
     * Get the player capturing the throne.
     * @return the holder of the ceremony.
     */
    @NotNull RocPlayer getPlayer();

    /**
     * Get the capture ratio.
     * @return a double between 0 and 1.
     */
    double getRatio();

    /**
     * Get the color of the capture.
     * @return a non-null string, usable by TAB.
     */
    @NotNull String getColor();

}
