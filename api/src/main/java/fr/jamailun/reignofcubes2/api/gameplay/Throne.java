package fr.jamailun.reignofcubes2.api.gameplay;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Represents the zone of the throne.
 */
public interface Throne {

    boolean isAlreadyInside(RocPlayer player);

    /**
     * Custom AABB test. Check if the location is inside the throne zone.
     * @param loc location to test
     * @return true if inside.
     */
    boolean isInside(Location loc);

    void enters(RocPlayer player);

    void leaves(RocPlayer player);

    boolean hasCeremony();

    void resetCeremony();

    boolean isCooldownOk(UUID uuid);

}
