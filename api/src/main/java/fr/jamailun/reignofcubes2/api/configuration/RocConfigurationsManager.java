package fr.jamailun.reignofcubes2.api.configuration;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Manages games configurations.
 */
public interface RocConfigurationsManager {

    /**
     * Get a copy of the configurations as a map.
     * @return a immuable map.
     */
    @NotNull Map<String, RocConfiguration> map();

    /**
     * Get a copy of the configurations as a list.
     * @return a immuable list.
     */
    @NotNull List<RocConfiguration> list();

    /**
     * Set the default configuration.
     * @param configurationName the name of the local configuration. If null, clear the field.
     */
    void setDefault(@Nullable String configurationName);

    /**
     * Get the default configuration on this server.
     * @return null if no valid configuration as been set.
     */
    @Nullable RocConfiguration getDefault();

    /**
     * Check if a specific configuration is the default one.
     * @param configuration the configuration to test.
     * @return true if this configuration is already the default one.
     */
    boolean isDefault(@NotNull RocConfiguration configuration);

    /**
     * Check if a default configuration has been set.
     * @return true if {@link #getDefault()} returns non-null.
     */
    boolean hasDefault();

    /**
     * Count the amount of configurations.
     * @return a non-negative integer.
     */
    int count();

    /**
     * Get a specific configuration.
     * @param name the unique name of the configuration to check.
     * @return null if the configuration does not exist.
     */
    @Nullable RocConfiguration get(@NotNull String name);

    /**
     * Test if a configuration with a specific name already exists.
     * @param name the name to test.
     * @return true if this configuration already exists.
     */
    boolean contains(@NotNull String name);

    /**
     * Create a new configuration.
     * @param name the name of the config. Must be unique.
     * @param author the author of the configuration.
     * @param world the world instance to apply the configuration.
     * @return null if an issue occurred.
     */
    @Nullable RocConfiguration createNewConfiguration(@NotNull String name, @NotNull String author, @NotNull World world);


}
