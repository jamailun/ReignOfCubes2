package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.players.PlayersManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class ReignOfCubes2 extends JavaPlugin {

    private static ReignOfCubes2 INSTANCE;

    private PlayersManager playersManager = new PlayersManager();
    @Getter private GameManager gameManager;

    @Override
    public void onEnable() {
        INSTANCE = this;
        Bukkit.getLogger().info("Enabling plugin.");

        gameManager = new GameManager(playersManager);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Disabling plugin.");
    }

    public static BukkitTask runTaskTimer(Runnable runnable, double periodSeconds) {
        long period = (long)( periodSeconds * 20L );
        return Bukkit.getScheduler().runTaskTimer(INSTANCE, runnable, 0L, period);
    }

}
