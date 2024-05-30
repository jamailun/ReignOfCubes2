package fr.jamailun.reignofcubes2.api.tags;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A tag holder is something that can hold a {@link RocTag}.
 */
public interface RocTagHolder {

    /**
     * Test if the holder has any tag.
     * @return true if it's the case.
     */
    boolean hasTag();

    /**
     * Set the tag of the holder.
     * @param tag the non-null tag to use.
     */
    void setTag(@NotNull RocTag tag);

    /**
     * Get the tag of this holder.
     * @return an empty optional if no tag is held.
     */
    Optional<RocTag> getTag();

    /**
     * Clear the tag of the holder.
     */
    void clearTag();

}
