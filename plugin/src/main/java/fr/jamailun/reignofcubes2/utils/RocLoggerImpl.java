package fr.jamailun.reignofcubes2.utils;

import fr.jamailun.reignofcubes2.api.utils.RocLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class RocLoggerImpl implements RocLogger {

    private final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("[HH:mm:ss]");

    private final String prefix;
    private final CommandSender sender;

    public RocLoggerImpl() {
        this(null);
    }

    private static @NotNull String time() {
        return TIME_FORMAT.format(Instant.now());
    }

    private final @NotNull String prefix(@NotNull String color) {
        return color + time() + "[ROC]";
    }

    public RocLoggerImpl(@Nullable String prefix) {
        this.prefix = Objects.requireNonNullElse(prefix, "");
        this.sender = Bukkit.getConsoleSender();
    }

    @Override
    public void debug(@NotNull String message) {
        sender.sendMessage(prefix("§3") + "[Debug]" + prefix + "§7 " + message);
    }

    @Override
    public void info(@NotNull String message) {
        sender.sendMessage(prefix("§b") + "[Info]" + prefix + "§f " + message);
    }

    @Override
    public void warn(@NotNull String message) {
        sender.sendMessage(prefix("§6") + "[Warn]" + prefix + "§e " + message);
    }

    @Override
    public void warn(@NotNull String message, @NotNull Throwable t) {
        warn(message);
        sender.sendMessage("§e" + formatError(t));
    }

    @Override
    public void error(@NotNull String message) {
        sender.sendMessage(prefix("§4") + "[Error]" + prefix + "§c " + message);
    }

    @Override
    public void error(@NotNull String message, @NotNull Throwable t) {
        error(message);
        sender.sendMessage("§c" + formatError(t));
    }

    private String formatError(@NotNull Throwable t) {
        StringBuilder sb = new StringBuilder();
        t.printStackTrace(new PrintWriter(new Writer() {
            @Override
            public void write(char @NotNull [] buf, int off, int len) {
                sb.append(String.copyValueOf(buf, off, len));
            }
            @Override
            public void write(@NotNull String str) {
                sb.append(str);
            }
            @Override public void flush() {}
            @Override public void close() {}
        }));
        return sb.toString();
    }


}
