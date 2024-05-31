package fr.jamailun.reignofcubes2.api.configuration.kits;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages all {@link Kit}.
 */
public interface KitsManager {

    /**
     * Reload all kits from the kit folder.
     */
    void reload();

    /**
     * Create a new empty kit.
     * @param id the ID of the kit. Must be unique.
     * @param displayName the display name to use. If null, the ID will be used.
     * @return a non-null kit.
     * @throws KitAlreadyExistsException if the ID is not unique.
     */
    @NotNull Kit create(@NotNull String id, @Nullable String displayName) throws KitAlreadyExistsException;

    /**
     * Try to find a specific kit.
     * @param id the ID of the kit to look for.
     * @return an empty optional if no kit was found with the ID.
     */
    @NotNull Optional<Kit> getKit(String id);

    /**
     * Get all the kits as a list.
     * @return a non-null list.
     */
    @NotNull List<Kit> getKits();

    /**
     * Delete a kit.
     * @param kit the kit to delete. Does nothing if null.
     */
    void delete(@Nullable Kit kit);

    /**
     * Get the default kit. I.e. the first kit of the {@link #getKits()} list with a price of <b>zero</b>.
     * @return null only if no kit has been set.
     */
    Kit getDefaultKit();
}
