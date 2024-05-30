package fr.jamailun.reignofcubes2.api;

import fr.jamailun.reignofcubes2.api.configuration.RocConfiguration;
import fr.jamailun.reignofcubes2.api.gameplay.Ceremony;
import fr.jamailun.reignofcubes2.api.gameplay.GameCountdown;
import fr.jamailun.reignofcubes2.api.music.MusicManager;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.sounds.SoundEffect;
import fr.jamailun.reignofcubes2.api.utils.Ranking;
import org.jetbrains.annotations.NotNull;

public interface GameManager {

    @NotNull GameState getState();

    boolean isStatePlaying();

    boolean isStateCountdown();

    void broadcast(String entry, Object... args);

    void playSound(SoundEffect soundEffect);

    boolean hasKing();

    RocPlayer getKing();

    int getOnlinePlayersCount();

    Ceremony getCeremony();

    GameCountdown getCountdown();

    @NotNull MusicManager getMusicManager();

    Ranking<RocPlayer> getRanking();

    RocConfiguration getConfiguration();

}
