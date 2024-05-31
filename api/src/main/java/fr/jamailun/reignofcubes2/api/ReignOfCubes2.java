package fr.jamailun.reignofcubes2.api;

import fr.jamailun.reignofcubes2.api.configuration.kits.KitsManager;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    public static RocPlayer findPlayer(Player player) {
        return service.findPlayer(player);
    }

    public static @NotNull String i18n(String language, String key, Object... vars) {
        return service.i18n(language, key, vars);
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
    public static @NotNull GameManager gameManager() {
        return service.gameManager();
    }

    public static void logDebug(String message) {
        service.logDebug(message);
    }

    public static void logInfo(String message) {
        service.logInfo(message);
    }

    public static void logWarning(String message) {
        service.logWarning(message);
    }

    public static void logError(String message) {
        service.logError(message);
    }

}
