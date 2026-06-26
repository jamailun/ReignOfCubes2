package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A tag represents an extra-equipment feature, provided by a kit.
 */
public abstract class Tag {

    private final String id;
    protected final ReignOfCubes2 plugin;

    /**
     * Create a new tag.
     * @param id the unique ID of this tag.
     * @param plugin the main plugin instance.
     */
    public Tag(@NotNull String id, @NotNull ReignOfCubes2 plugin) {
        this.id = id;
        this.plugin = plugin;
    }

    /**
     * Triggered when the tag is applied to a new holder.
     * @param holder the holder of the tag.
     */
    public abstract void added(@NotNull RocPlayer holder);

    /**
     * Triggered when the tag is removed from a holder.
     * @param holder the ex-holder of the tag.
     */
    public abstract void removed(@NotNull RocPlayer holder);

    /**
     * The holder of the tags inflicts damages to another player.
     * @param holder the non-null holder of the tag.
     * @param victim the non-null player victim of the attack.
     * @param event the relative bukkit event.
     */
    public abstract void holderAttacks(@NotNull RocPlayer holder, @NotNull RocPlayer victim, @NotNull EntityDamageByEntityEvent event);

    /**
     * The holder of the tag has been damaged.
     * @param holder the non-null holder of the tag.
     * @param attacker the attacker. Null if damage is from environment.
     * @param event the relative bukkit event.
     */
    public abstract void holderDefends(@NotNull RocPlayer holder, @Nullable RocPlayer attacker, @NotNull EntityDamageEvent event);

    /**
     * Get the unique ID of the Tag.
     * @return a non-null string.
     */
    public final @NotNull String getId() {
        return id;
    }

    /**
     * Check a player has this tag.
     * @param player player to check.
     * @return true if player has the tag.
     */
    protected boolean is(@NotNull RocPlayer player) {
        return player.getTag().map(tag -> tag.getId().equals(id)).orElse(false);
    }

    /**
     * Check a player has this tag.
     * @param player player to check.
     * @return true if player has the tag.
     */
    protected boolean is(@NotNull Player player) {
        RocPlayer hPlayer = plugin.getGameManager().toPlayer(player);
        return hPlayer != null && is(hPlayer);
    }

    /**
     * Map a bukkit player to a ROC player.
     * @param player player to use.
     * @return null if the player is not in the game.
     */
    protected @Nullable RocPlayer getRocPlayer(@NotNull Player player) {
        return plugin.getGameManager().toPlayer(player);
    }

}
