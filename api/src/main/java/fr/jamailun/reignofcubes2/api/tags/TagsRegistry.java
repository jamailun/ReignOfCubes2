package fr.jamailun.reignofcubes2.api.tags;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for tags.
 * @see #register(Plugin, RocTag)
 */
public final class TagsRegistry {
    private TagsRegistry() {}

    private static final Map<String, RocTag> tags = new HashMap<>();

    /**
     * Register a new tag.
     * @param tag the tag to add. Will override tags with same ID.
     */
    public static void register(Plugin plugin, @NotNull RocTag tag) {
        tags.put(tag.getId(), tag);
        ReignOfCubes2.logInfo("New tag registered : '" + tag.getId() + "'.");
        Bukkit.getPluginManager().registerEvents(tag, plugin);
    }

    /**
     * Find a tag from uts ID.
     * @param id the ID of the tag to find. If null, returns null.
     * @return null if no tags exist with this ID.
     */
    public static @Nullable RocTag find(@Nullable String id) {
        if(id == null) return null;
        return tags.get(id);
    }

}
