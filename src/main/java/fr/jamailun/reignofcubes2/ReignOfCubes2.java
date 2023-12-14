package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.commands.*;
import fr.jamailun.reignofcubes2.configuration.KitsConfiguration;
import fr.jamailun.reignofcubes2.listeners.*;
import fr.jamailun.reignofcubes2.placeholder.RocPlaceholderExpansion;
import io.papermc.paper.plugin.configuration.PluginMeta;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class ReignOfCubes2 extends JavaPlugin {

    private static ReignOfCubes2 INSTANCE;

    @Getter private GameManager gameManager;
    private KitsConfiguration kitsConfiguration;

    @Override
    public void onEnable() {
        INSTANCE = this;
        ReignOfCubes2.info("Enabling plugin.");
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            warning("Could not find PlaceholderAPI. Disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Load kits
        kitsConfiguration = new KitsConfiguration(getFile("kits.yml"));

        // default config
        saveDefaultConfig();

        Bukkit.getScheduler().runTaskLater(this, this::enableRoc, 20L);
    }

    private void enableRoc() {
        // Game manager
        gameManager = new GameManager();

        // Listeners
        new PlayerConnectionListener(this);
        new PlayerMovementListener(this);
        new PlayerDeathListener(this);
        new DisabledActionsListener(this);
        new PlayerRespawnListener(this);
        new RocScoreListener(this);
        new GuiListener(this);

        // Commands
        new RocCommand(this);
        new ShopCommand(this);

        // Placeholder API
        new RocPlaceholderExpansion(gameManager).register();

        // Reload
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            gameManager.playerJoinsServer(onlinePlayer);
        }
    }

    @Override
    public void onDisable() {
        ReignOfCubes2.info("Disabling plugin.");
    }

    public static BukkitTask runTaskTimer(Runnable runnable, double periodSeconds) {
        long period = (long)( periodSeconds * 20L );
        return Bukkit.getScheduler().runTaskTimer(INSTANCE, runnable, 0L, period);
    }

    public static BukkitTask runTaskLater(Runnable runnable, double waitSeconds) {
        long wait = (long)( waitSeconds * 20L );
        return Bukkit.getScheduler().runTaskLater(INSTANCE, runnable, wait);
    }

    public static File getFile(String name) {
        return new File(INSTANCE.getDataFolder(), name);
    }

    public static void info(String msg) {
        INSTANCE.getLogger().info(msg);
    }
    public static void warning(String msg) {
        INSTANCE.getLogger().warning(msg);
    }
    public static void error(String msg) {
        INSTANCE.getLogger().severe(msg);
    }

    public static ConfigurationSection getDefaultConfiguration() {
        return INSTANCE.getConfig();
    }

    public static void saveDefaultConfiguration() {
        INSTANCE.saveConfig();
    }

    public static PluginMeta getMeta() {
        return INSTANCE.getPluginMeta();
    }

    public static KitsConfiguration getKits() {
        return INSTANCE.kitsConfiguration;
    }

}
