package fr.jamailun.reignofcubes2.api.gameplay;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Something that can be captured by a {@link RocPlayer}.
 */
public interface CapturableRegion extends Region {

    /**
     * Get the current owner of this object, if exists.
     * @return null if this object does not have any owner.
     */
    @Nullable RocPlayer getOwner();

    default boolean hasCaptureOngoing() {
        return getCaptureProcess() != null;
    }

    @Nullable CaptureProcess getCaptureProcess();

    default boolean isOwner(@NotNull RocPlayer player) {
        return Objects.equals(getOwner(), player);
    }

}
