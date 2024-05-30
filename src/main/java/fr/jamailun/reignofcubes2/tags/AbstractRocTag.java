package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.api.tags.RocTag;
import org.jetbrains.annotations.NotNull;

/**
 * A tag represents an extra-equipment feature, provided by a kit.
 */
abstract class AbstractRocTag implements RocTag {

    private final String id;

    /**
     * Create a new tag.
     * @param id the unique ID of this tag.
     */
    public AbstractRocTag(@NotNull String id) {
        this.id = id;
    }

    @Override
    public final @NotNull String getId() {
        return id;
    }

}
