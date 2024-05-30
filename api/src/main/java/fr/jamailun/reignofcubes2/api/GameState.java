package fr.jamailun.reignofcubes2.api;

/**
 * The state of a game.
 */
public enum GameState {

    /**
     * Configuration error.
     */
    NOT_CONFIGURED,

    /**
     * Currently waiting for more players to join.
     */
    WAITING,

    /**
     * Counting down before starting.
     */
    COUNT_DOWN,

    /**
     * Currently playing.
     */
    PLAYING
}
