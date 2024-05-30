package fr.jamailun.reignofcubes2.api;

import org.jetbrains.annotations.NotNull;

public interface RocGame {

    @NotNull GameState getState();

    boolean isPlaying();

}
