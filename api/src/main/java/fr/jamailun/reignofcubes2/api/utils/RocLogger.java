package fr.jamailun.reignofcubes2.api.utils;

import org.jetbrains.annotations.NotNull;

/**
 * Logger for ROC.
 */
public interface RocLogger {

    void debug(@NotNull String message);

    void info(@NotNull String message);

    void warn(@NotNull String message);
    void warn(@NotNull String message, @NotNull Throwable t);

    void error(@NotNull String message);
    void error(@NotNull String message, @NotNull Throwable t);

}
