package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.api.configuration.PropertiesHolder;
import fr.jamailun.reignofcubes2.api.tags.RocTag;
import fr.jamailun.reignofcubes2.api.tags.TagName;
import org.jetbrains.annotations.NotNull;

/**
 * A tag represents an extra-equipment feature, provided by a kit.
 */
public abstract class AbstractRocTag extends PropertiesHolder implements RocTag {

    private final String id;

    /**
     * Create a new tag.
     */
    public AbstractRocTag() {
        TagName nameAnnotation = getClass().getAnnotation(TagName.class);
        this.id = nameAnnotation == null ? getClass().getSimpleName().toLowerCase() : nameAnnotation.value();
    }

    @Override
    public final @NotNull String getId() {
        return id;
    }

}
