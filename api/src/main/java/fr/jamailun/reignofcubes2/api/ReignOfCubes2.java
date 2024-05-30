package fr.jamailun.reignofcubes2.api;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ReignOfCubes2 {
    private ReignOfCubes2() {}

    private static RocService service;

    public static void setService(RocService service) {
        if(ReignOfCubes2.service != null) {
            throw new IllegalStateException("Cannot set the ROC2 service : has already been set.");
        }
        ReignOfCubes2.service = service;
    }

    public static RocPlayer findPlayer(Player player) {
        return service.findPlayer(player);
    }

    public static @NotNull String getI18n(String language, String key, Object... vars) {
        return service.getI18n(language, key, vars);
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
