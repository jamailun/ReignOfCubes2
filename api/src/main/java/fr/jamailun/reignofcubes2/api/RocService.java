package fr.jamailun.reignofcubes2.api;

import fr.jamailun.reignofcubes2.api.configuration.kits.KitsManager;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.utils.RocLogger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface RocService {

    @Nullable RocPlayer findPlayer(@NotNull Player player);

    @NotNull String i18n(@NotNull String language, @NotNull String key, Object... vars);

    @NotNull KitsManager kits();

    @NotNull GameManager gameManager();

    @NotNull RocLogger logger();

    @NotNull List<RocPlayer> players();

    @NotNull GameState state();

    void broadcast(@NotNull String entry, @NotNull Object... args);

    Location getLobby();

    void setLobby(@NotNull Location lobby);
}
