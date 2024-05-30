package fr.jamailun.reignofcubes2.api.music;

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

    void removePlayer(Player player) ;

    void removePlayer(UUID uuid);

    void addPlayer(Player player, MusicType type);

    void addPlayer(UUID uuid, MusicType type);

    boolean hasRadioFor(MusicType type);

    @NotNull String getHeardSongTitle(@NotNull Player player);

}
