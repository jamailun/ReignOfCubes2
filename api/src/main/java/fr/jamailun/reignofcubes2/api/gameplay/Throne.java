package fr.jamailun.reignofcubes2.api.gameplay;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Represents the zone of the throne.
 */
public interface Throne extends CapturableRegion {

    void resetCapture();

    //FIXME
    boolean isCooldownOk(UUID uuid);

}
