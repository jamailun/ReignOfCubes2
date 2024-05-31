package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.RocService;
import fr.jamailun.reignofcubes2.api.configuration.kits.KitsManager;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.commands.*;
import fr.jamailun.reignofcubes2.configuration.KitsConfigurationManager;
import fr.jamailun.reignofcubes2.configuration.sections.TagsConfigurationSection;
import fr.jamailun.reignofcubes2.configuration.GameConfiguration;
import fr.jamailun.reignofcubes2.configuration.kits.RocKitItem;
import fr.jamailun.reignofcubes2.listeners.*;
import fr.jamailun.reignofcubes2.music.MusicManagerImpl;
import fr.jamailun.reignofcubes2.placeholder.RocPlaceholderExpansion;
import fr.jamailun.reignofcubes2.tags.NinjaTag;
import fr.jamailun.reignofcubes2.tags.RegicideTag;
import fr.jamailun.reignofcubes2.tags.StealerTag;
import fr.jamailun.reignofcubes2.api.tags.TagsRegistry;
import io.papermc.paper.plugin.configuration.PluginMeta;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Main of the ReignOfCubes2 implementation.
 */
public final class MainROC2 extends JavaPlugin implements RocService {

    private static MainROC2 INSTANCE;

    @Getter private GameManagerImpl gameManager;
    @Getter private MusicManagerImpl musicManager;
    private KitsConfigurationManager kitsConfiguration;
    private NamespacedKey marker;

    // Register serializable
    static {
        ConfigurationSerialization.registerClass(RocKitItem.class, "KitItem");
    }

    @Override
    public void onLoad() {
        ReignOfCubes2.setService(this);
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        logInfo("Enabling plugin.");
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logWarning("Could not find PlaceholderAPI. Disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Load musics
        musicManager = new MusicManagerImpl(getFile("musics"));

        // Load tags
        TagsRegistry.register(this, new RegicideTag());
        TagsRegistry.register(this, new NinjaTag());
        TagsRegistry.register(this, new StealerTag());

        // Load kits
        kitsConfiguration = new KitsConfigurationManager(getFile("kits"));

        // default config
        saveDefaultConfig();

        Bukkit.getScheduler().runTaskLater(this, this::enableRoc, 20L);
    }

    private void enableRoc() {
        // Game manager
        gameManager = new GameManagerImpl(musicManager);

        // Listeners
        new PlayerConnectionListener(this);
        new PlayerMovementListener(this);
        new PlayerDeathListener(this);
        new PlayerDamageListener(this);
        new PlayerInteractionListener(this);
        new DisabledActionsListener(this);
        new PlayerRespawnListener(this);
        new PlayerSaturationChangedListener(this);
        new RocScoreListener(this);
        new GuiListener(this);
        new PlayerPickupListener(this);

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
        logInfo("Disabling plugin.");
        gameManager.purge();
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

    public static ConfigurationSection getDefaultConfiguration() {
        return INSTANCE.getConfig();
    }

    public static void saveDefaultConfiguration() {
        INSTANCE.saveConfig();
    }

    public static PluginMeta getMeta() {
        return INSTANCE.getPluginMeta();
    }

    @Override
    public RocPlayer findPlayer(Player player) {
        return null;
    }

    @Override
    public @NotNull String getI18n(String language, String key, Object... vars) {
        return null;
    }

    @Override
    public void logDebug(String message) {
        getLogger().info("[DEBUG] " + message);
    }

    @Override
    public void logInfo(String message) {
        getLogger().info(message);
    }

    @Override
    public void logWarning(String message) {
        getLogger().warning(message);
    }

    @Override
    public void logError(String message) {
        getLogger().severe(message);
    }

    @Override
    public @NotNull KitsManager getKits() {
        return kitsConfiguration;
    }

    public static TagsConfigurationSection getTags() {
        return INSTANCE.gameManager.getTagsConfiguration();
    }

    public static GameConfiguration getCurrentConfig() {
        return INSTANCE.gameManager.getConfiguration();
    }

    public static boolean isPlaying() {
        return INSTANCE.gameManager.isStatePlaying();
    }

    public static void updateRanks(RocPlayer player) {
        INSTANCE.gameManager.getRanking().update(player);
    }

    public static NamespacedKey marker() {
        if(INSTANCE.marker == null) {
            INSTANCE.marker = new NamespacedKey(INSTANCE, "marker");
        }
        return INSTANCE.marker;
    }

    public static Plugin plugin() {
        return INSTANCE;
    }

}
