package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.commands.RocCommand;
import fr.jamailun.reignofcubes2.listeners.PlayerConnectionListener;
import fr.jamailun.reignofcubes2.listeners.PlayerMovementListener;
import fr.jamailun.reignofcubes2.players.PlayersManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class ReignOfCubes2 extends JavaPlugin {

    private static ReignOfCubes2 INSTANCE;

    @Getter private GameManager gameManager;

    @Override
    public void onEnable() {
        INSTANCE = this;
        Bukkit.getLogger().info("Enabling plugin.");
        // default config
        saveDefaultConfig();

        // Game manager
        gameManager = new GameManager(new PlayersManager(), getConfig());

        // Listeners
        new PlayerConnectionListener(this);
        new PlayerMovementListener(this);

        // Commands
        new RocCommand(this);

        // Reload
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            gameManager.playerJoinsServer(onlinePlayer);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Disabling plugin.");
    }

    public static BukkitTask runTaskTimer(Runnable runnable, double periodSeconds) {
        long period = (long)( periodSeconds * 20L );
        return Bukkit.getScheduler().runTaskTimer(INSTANCE, runnable, 0L, period);
    }

    public static File getFile(String name) {
        return new File(INSTANCE.getDataFolder(), name);
    }
}
