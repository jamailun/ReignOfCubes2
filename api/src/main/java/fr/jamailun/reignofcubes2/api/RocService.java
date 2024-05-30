package fr.jamailun.reignofcubes2.api;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RocService {
    RocPlayer findPlayer(Player player);

    @NotNull String getI18n(String language, String key, Object... vars);

    void logInfo(String message);

    void logWarning(String message);

    void logError(String message);

}
