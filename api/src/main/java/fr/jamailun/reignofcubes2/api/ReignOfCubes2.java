package fr.jamailun.reignofcubes2.api;

import fr.jamailun.reignofcubes2.api.configuration.kits.KitsManager;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.utils.RocLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Entry point to the ROC2 API.
 */
public final class ReignOfCubes2 {
    private ReignOfCubes2() {}

    private static RocService service;

    public static void setService(RocService service) {
        if(ReignOfCubes2.service != null) {
            throw new IllegalStateException("Cannot set the ROC2 service : has already been set.");
        }
        ReignOfCubes2.service = service;
    }

    /**
     * Find a ROC player wrapper.
     * @param player the Bukkit player to find the wrapper of.
     * @return a ROC wrapper of this player.
     */
    public static @Nullable RocPlayer findPlayer(Player player) {
        return service.findPlayer(player);
    }

    public static @NotNull String i18n(@NotNull String language, @NotNull String key, Object... vars) {
        return service.i18n(language, key, vars);
    }

    public static @NotNull List<RocPlayer> players() {
        return service.players();
    }

    /**
     * Obtain a reference to the KitsManager singleton.
     * @return the reference to the kits-manager.
     */
    public static @NotNull KitsManager kits() {
        return service.kits();
    }

    /**
     * Obtain a reference to the GameManager singleton.
     * @return the reference to the game-manager.
     */
    public static @NotNull GameManager game() {
        return service.gameManager();
    }

    /**
     * Get the ROC logger.
     * @return the non-null logger instance of the service.
     */
    public static @NotNull RocLogger logger() {
        return service.logger();
    }

    /**
     * Get the game state.
     * @return the non-null current state of the game.
     */
    public static @NotNull GameState state() {
        return service.state();
    }

}
