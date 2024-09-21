package fr.jamailun.reignofcubes2.api.music;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Manages musics for players.
 */
public interface MusicManager {

    /**
     * Reload music playlists, but keep listeners in place.
     */
    void reload();

    default void removePlayer(@NotNull RocPlayer player) {
        removePlayer(player.getPlayer());
    }

    void removePlayer(@NotNull Player player) ;

    void removePlayer(@NotNull UUID uuid);

    default void addPlayer(@NotNull RocPlayer player, @NotNull MusicType type) {
        addPlayer(player.getPlayer(), type);
    }

    void addPlayer(@NotNull Player player, @NotNull MusicType type);

    void addPlayer(@NotNull UUID uuid, @NotNull MusicType type);

    boolean hasRadioFor(@NotNull MusicType type);

    @NotNull String getHeardSongTitle(@NotNull Player player);

}
