package fr.jamailun.reignofcubes2.api.entities;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An entity damageable by a player.
 */
public interface RocDamageable {

    /**
     * Try to get the last damager of this player.
     * @return null if no damages was dealt to this instance recently.
     */
    @Nullable RocPlayer getLastDamager();

    /**
     * Mark this entity as last damaged by a player.
     * @param player the player to use.
     */
    void setLastDamager(@NotNull RocPlayer player);

}
