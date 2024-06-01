package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.configuration.BadConfigurationException;
import fr.jamailun.reignofcubes2.api.configuration.RocConfiguration;
import fr.jamailun.reignofcubes2.api.configuration.RocConfigurationsManager;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Configurations manager.
 */
public class GameConfigurationsManager implements RocConfigurationsManager {

    private final File directory;
    private GameConfiguration defaultConfiguration;
    private final Map<String, GameConfiguration> configurations = new HashMap<>();

    public GameConfigurationsManager() {
        // Load configurations
        directory = MainROC2.getFile("configurations");
        assert directory.exists() || (directory.mkdirs() && directory.mkdir()) : "Could not generate directories to '"+directory+"'";
        ReignOfCubes2.logInfo("CONFIG_LIST = " + directory);

        File[] files = directory.listFiles();
        if(files == null || files.length == 0) {
            ReignOfCubes2.logWarning("No configuration exist at all.");
            return;
        }

        for(File file : files) {
            if( !isYaml(file)) continue;
            try {
                GameConfiguration configuration = new GameConfiguration(file);
                configurations.put(configuration.getName(), configuration);
            } catch (BadConfigurationException e) {
                ReignOfCubes2.logError("Could not load file " + file + " : " + e.getMessage());
            }
        }

        // Load default
        ConfigurationSection config = MainROC2.getDefaultConfiguration();
        String defaultName = config.getString("default");
        if(defaultName == null) {
            ReignOfCubes2.logWarning("No default configuration set.");
            return;
        }
        GameConfiguration configuration = get(defaultName);
        if(configuration == null) {
            ReignOfCubes2.logError("Unknown default configuration: '" + defaultName + "'.");
            return;
        }
        if(!configuration.isValid()) {
            ReignOfCubes2.logError("Invalid default configuration: '" + defaultName + "'.");
            return;
        }
        defaultConfiguration = configuration;
    }

    @Override
    public @NotNull Map<String, RocConfiguration> map() {
        return Collections.unmodifiableMap(configurations);
    }

    @Override
    public @NotNull List<RocConfiguration> list() {
        return configurations.values()
                .stream()
                .sorted(Comparator.comparing(RocConfiguration::getName))
                .map(RocConfiguration.class::cast)
                .toList();
    }

    @Override
    public void setDefault(@Nullable String configurationName) {
        if(configurationName == null) {
            defaultConfiguration = null;
            MainROC2.getDefaultConfiguration().set("default", null);
            return;
        }

        // validity
        GameConfiguration config = get(configurationName);
        if(config == null || ! config.isValid()) {
            ReignOfCubes2.logError("Cannot set "+ configurationName + " has default configuration : it is invalid/null.");
            return;
        }
        // save
        defaultConfiguration = config;
        MainROC2.getDefaultConfiguration().set("default", configurationName);
        MainROC2.saveDefaultConfiguration();
    }

    @Override
    public @Nullable GameConfiguration getDefault() {
        return defaultConfiguration;
    }

    @Override
    public boolean isDefault(@NotNull RocConfiguration configuration) {
        return defaultConfiguration != null && defaultConfiguration.getName().equalsIgnoreCase(configuration.getName());
    }

    @Override
    public boolean hasDefault() {
        return defaultConfiguration != null;
    }

    @Override
    public int count() {
        return configurations.size();
    }

    @Override
    public GameConfiguration get(@NotNull String name) {
        return configurations.get(name);
    }

    @Override
    public boolean contains(@NotNull String name) {
        return configurations.containsKey(name);
    }

    @Override
    public @Nullable RocConfiguration createNewConfiguration(@NotNull String name, @NotNull String author, @NotNull World world) {
        assert ! contains(name) : "This configuration already exists";
        File file = new File(directory, name + ".yml");
        if(! file.exists()) {
            try {
                assert file.createNewFile();
            } catch(IOException e) {
                throw new RuntimeException("Could not create file " + file + ": ", e);
            }
        }

        GameConfiguration configuration = new GameConfiguration(file, name, author, world.getName());
        try {
            configuration.save();
        } catch(IOException e) {
            ReignOfCubes2.logError("Could not save " + configuration + ": " + e);
            return null;
        }
        configurations.put(name, configuration);
        return configuration;
    }

    private static boolean isYaml(File file) {
        if(!file.isFile() || !file.exists())
            return false;
        String path = file.getAbsolutePath().toLowerCase();
        return path.endsWith(".yml") || path.endsWith(".yaml");
    }

}
