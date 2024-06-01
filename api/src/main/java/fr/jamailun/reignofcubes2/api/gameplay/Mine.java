package fr.jamailun.reignofcubes2.api.gameplay;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * A mine is a producer of gold.
 */
public interface Mine {

    @NotNull Location getLocation();

    double getRadius();

    boolean hasOwner();

    RocPlayer getOwner();

}
