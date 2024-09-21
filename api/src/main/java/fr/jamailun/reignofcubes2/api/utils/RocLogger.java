package fr.jamailun.reignofcubes2.api.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Logger for ROC.
 */
public interface RocLogger {

    @NotNull RocLogger clone(@Nullable String prefix);

    void debug(@NotNull String message);

    void info(@NotNull String message);

    void warn(@NotNull String message);
    void warn(@NotNull String message, @NotNull Throwable t);

    void error(@NotNull String message);
    void error(@NotNull String message, @NotNull Throwable t);

}
