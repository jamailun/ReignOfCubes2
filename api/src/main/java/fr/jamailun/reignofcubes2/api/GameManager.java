package fr.jamailun.reignofcubes2.api;

import fr.jamailun.reignofcubes2.api.configuration.RocConfiguration;
import fr.jamailun.reignofcubes2.api.gameplay.Ceremony;
import fr.jamailun.reignofcubes2.api.gameplay.GameCountdown;
import fr.jamailun.reignofcubes2.api.music.MusicManager;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.sounds.SoundEffect;
import fr.jamailun.reignofcubes2.api.utils.Ranking;
import org.jetbrains.annotations.NotNull;

/**
 * Manages the current state of the ROC game.
 */
public interface GameManager {

    @NotNull GameState getState();

    boolean isStatePlaying();

    boolean isStateCountdown();

    void broadcast(String entry, Object... args);

    void playSound(SoundEffect soundEffect);

    /**
     * Test if the game currently has a King.
     * @return false if no king is present.
     */
    boolean hasKing();

    /**
     * Get the current king.
     * @return null if no king has been set.
     * @see #hasKing()
     */
    RocPlayer getKing();

    int getOnlinePlayersCount();

    Ceremony getCeremony();

    GameCountdown getCountdown();

    /**
     * Get the musics-manager.
     * @return a non-ull reference to the singleton.
     */
    @NotNull MusicManager getMusicManager();

    /**
     * Get the rankings.
     * @return null if the game did not start.
     */
    Ranking<RocPlayer> getRanking();

    /**
     * Update the rank of a specific player.
     * @param player the non-ull player to recompute ranking of.
     */
    void updateRankings(@NotNull RocPlayer player);

    /**
     * Get the <b>current</b> configuration.
     * @return a configuration. Can be null if no configuration has been set.
     */
    RocConfiguration getConfiguration();

}
