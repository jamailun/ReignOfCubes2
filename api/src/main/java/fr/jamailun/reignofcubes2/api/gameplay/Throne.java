package fr.jamailun.reignofcubes2.api.gameplay;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
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

    boolean hasCeremony();

    void resetCeremony();

    @Nullable Ceremony getCeremony();

    boolean isCooldownOk(UUID uuid);

}
