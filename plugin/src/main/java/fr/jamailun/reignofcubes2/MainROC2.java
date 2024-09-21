package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.api.GameManager;
import fr.jamailun.reignofcubes2.api.GameState;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.RocService;
import fr.jamailun.reignofcubes2.api.configuration.kits.KitsManager;
import fr.jamailun.reignofcubes2.api.configuration.sections.RocConfigurationSectionsRegistry;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.utils.RocLogger;
import fr.jamailun.reignofcubes2.commands.*;
import fr.jamailun.reignofcubes2.configuration.KitsConfigurationManager;
import fr.jamailun.reignofcubes2.configuration.kits.RocKitItem;
import fr.jamailun.reignofcubes2.configuration.sections.GameRulesSection;
import fr.jamailun.reignofcubes2.configuration.sections.TagsConfigurationSection;
import fr.jamailun.reignofcubes2.configuration.sections.WorldSection;
import fr.jamailun.reignofcubes2.listeners.*;
import fr.jamailun.reignofcubes2.messages.Messages;
import fr.jamailun.reignofcubes2.music.MusicManagerImpl;
import fr.jamailun.reignofcubes2.placeholder.RocPlaceholderExpansion;
import fr.jamailun.reignofcubes2.tags.NinjaTag;
import fr.jamailun.reignofcubes2.tags.RegicideTag;
import fr.jamailun.reignofcubes2.tags.StealerTag;
import fr.jamailun.reignofcubes2.api.tags.TagsRegistry;
import fr.jamailun.reignofcubes2.utils.RocLoggerImpl;
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
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * Main of the ReignOfCubes2 implementation.
 */
public final class MainROC2 extends JavaPlugin implements RocService {

    private static MainROC2 INSTANCE;

    @Getter private GameManagerImpl gameManager;
    @Getter private MusicManagerImpl musicManager;
    private KitsConfigurationManager kitsConfiguration;
    private NamespacedKey marker;
    private RocLogger logger;

    static {
        // Register serializable
        ConfigurationSerialization.registerClass(RocKitItem.class, "KitItem");
    }

    @Override
    public void onLoad() {
        logger = new RocLoggerImpl();
        ReignOfCubes2.setService(this);
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        logger().info("Enabling plugin.");
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logger().warn("Could not find PlaceholderAPI. Disabling.");
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
        logger.info("Disabling plugin.");
        gameManager.purge();
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

    @SuppressWarnings("all")
    public static @NotNull PluginMeta getMeta() {
        return INSTANCE.getPluginMeta();
    }

    @Override
    public @Nullable RocPlayer findPlayer(@NotNull Player player) {
        return null;
    }

    @Override
    public @NotNull String i18n(@NotNull String language, @NotNull String key, Object... vars) {
        return Messages.format(language, key, vars);
    }

    @Override
    public @NotNull KitsManager kits() {
        return kitsConfiguration;
    }

    @Override
    public @NotNull GameManager gameManager() {
        return gameManager;
    }

    @Override
    public @NotNull RocLogger logger() {
        return logger;
    }

    @Override
    public @NotNull List<RocPlayer> players() {
        return gameManager.
    }

    @Override
    public @NotNull GameState state() {
        return gameManager.getState();
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
