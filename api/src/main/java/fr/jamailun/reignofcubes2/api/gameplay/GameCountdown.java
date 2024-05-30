package fr.jamailun.reignofcubes2.api.gameplay;

/**
 * Handles the countdown before the game.
 */
public interface GameCountdown {

    void cancel();

    int getRemainingSeconds();

}
