package fr.jamailun.reignofcubes2.api.configuration.kits;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.tags.RocTag;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * A kit is a set of items and other properties.
 * <br/>
 * Can be bought by a {@link RocPlayer} in-game.
 */
public interface Kit {

    /**
     * The unique ID of the kit.
     * @return a non-null configuration ID.
     * @see #getDisplayName()
     */
    @NotNull String getId();

    /**
     * Get the display name of the Kit.
     * @return a non-null string. Can have color tags.
     */
    @NotNull String getDisplayName();

    /**
     * Set the display-name.
     * @param name if null, the raw ID will be used to display.
     */
    void setDisplayName(String name);

    /**
     * Get the cost of the Kit.
     * @return negative value if not set, 0 if free.
     */
    int getCost();

    /**
     * Set the cost of the Kit.
     * @param cost the cost to use.
     */
    void setCost(int cost);

    /**
     * Get the Tag linked to this kit.
     * @return a nullable tag.
     */
    @Nullable RocTag getTag();

    /**
     * Change the tag of this kit.
     * @param tagId if null, clear the tag.
     */
    void setTag(@Nullable String tagId);

    /**
     * Get the icon-type representing the item.
     * @return a non-null material. Returns {@link Material#GRASS_BLOCK} by default.
     * @see #toIcon()
     */
    @NotNull Material getIconType();

    /**
     * Set the icon type.
     * @param iconType if null, will be considered as {@link Material#GRASS_BLOCK}.
     * @see #toIcon()
     */
    void setIconType(@Nullable Material iconType);

    /**
     * Reload this kit from the file.
     */
    void reload();

    /**
     * Set the Kit content with the inventory of a player entity.
     * @param player the Bukkit entity to get the inventory from.
     */
    void setFromInventory(@NotNull Player player);

    /**
     * Save the current kit content to the file.
     */
    void save();

    /**
     * Get the amount of items saved in the kit.
     * @return a non-negative integer.
     */
    int size();

    /**
     * get the Kit's icon.
     * @return a non-null item-stack ready to be displayed.
     */
    @NotNull ItemStack toIcon();

    /**
     * Make a player equip the kit.
     * <br/>
     * Current equipment will be cleared.
     * @param player a non-null Roc Player.
     */
    void equip(@NotNull RocPlayer player);

    /**
     * List items of the kit.
     * @param equipment if true, only return the equipment (armor). If false, returns everything else.
     * @return a non-null list of items.
     */
    @NotNull List<ItemStack> listItems(boolean equipment);

    /**
     * Archive the file to a folder (to avoid complete suppression).
     * @param folder the parent folder.
     */
    void archiveFile(@NotNull File folder);

}
