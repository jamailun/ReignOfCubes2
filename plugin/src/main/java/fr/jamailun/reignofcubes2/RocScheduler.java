package fr.jamailun.reignofcubes2;

import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class RocScheduler {

    @Setter(AccessLevel.PACKAGE)
    private static Plugin plugin;

    public static long secsToTicks(double seconds) {
        return (long)( seconds * 20d );
    }

    public static @NotNull BukkitTask runTaskTimer(Runnable runnable, double periodSeconds) {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, 0L, secsToTicks(periodSeconds));
    }

    public static @NotNull BukkitTask runTaskLater(Runnable runnable, double waitSeconds) {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, secsToTicks(waitSeconds));
    }

    public static @NotNull BukkitTask runTaskTimerAsync(Runnable runnable, double periodSeconds) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0L, secsToTicks(periodSeconds));
    }

    public static @NotNull BukkitTask runTaskLaterAsync(Runnable runnable, double waitSeconds) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, secsToTicks(waitSeconds));
    }

}
