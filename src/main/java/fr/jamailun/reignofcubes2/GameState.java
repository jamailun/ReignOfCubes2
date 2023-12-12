package fr.jamailun.reignofcubes2;

public enum GameState {

    /**
     * Configuration error.
     */
    NOT_CONFIGURED,

    /**
     * Currently waiting for more players to join.
     */
    WAITING,
    
    COUNT_DOWN,

    /**
     * Currently playing.
     */
    PLAYING
}
