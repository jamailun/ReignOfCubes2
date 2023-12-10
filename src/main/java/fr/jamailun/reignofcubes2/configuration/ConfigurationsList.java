package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigurationsList {

    private final File directory;
    private WorldConfiguration defaultConfiguration;
    private final Map<String, WorldConfiguration> configurations = new HashMap<>();

    public ConfigurationsList() {
        // Load configurations
        directory = ReignOfCubes2.getFile("configurations");
        assert directory.exists() || (directory.mkdirs() && directory.mkdir()) : "Could not generate directories to '"+directory+"'";
        ReignOfCubes2.info("CONFIG_LIST = " + directory);

        File[] files = directory.listFiles();
        if(files == null || files.length == 0) {
            ReignOfCubes2.warning("No configuration exist at all.");
            return;
        }

        for(File file : files) {
            if( !isYaml(file)) continue;
            try {
                WorldConfiguration configuration = WorldConfiguration.load(file);
                configurations.put(configuration.getName(), configuration);
            } catch (WorldConfiguration.BadWorldConfigurationException e) {
                ReignOfCubes2.error("Could not load file " + file + " : " + e.getMessage());
            }
        }

        // Load default
        ConfigurationSection config = ReignOfCubes2.getDefaultConfiguration();
        String defaultName = config.getString("default");
        if(defaultName == null) {
            ReignOfCubes2.warning("No default configuration set.");
            return;
        }
        WorldConfiguration configuration = get(defaultName);
        if(configuration == null) {
            ReignOfCubes2.error("Unknown default configuration: '" + defaultName + "'.");
            return;
        }
        if(!configuration.isValid()) {
            ReignOfCubes2.error("Invalid default configuration: '" + defaultName + "'.");
            return;
        }
        defaultConfiguration = configuration;
    }

    public Map<String, WorldConfiguration> getConfigurations() {
        return Collections.unmodifiableMap(configurations);
    }

    public List<WorldConfiguration> list() {
        return configurations.values()
                .stream()
                .sorted(Comparator.comparing(WorldConfiguration::getName))
                .toList();
    }

    public void setDefault(@Nullable WorldConfiguration configuration) {
        // validity
        if(configuration != null) {
            if(!configuration.isValid()) {
                ReignOfCubes2.error("Cannot set "+ configuration.getName() + " has default configuration : it is invalid.'");
                return;
            }
        }
        // save
        defaultConfiguration = configuration;
        ReignOfCubes2.getDefaultConfiguration().set("default", configuration.getName());
        ReignOfCubes2.saveDefaultConfiguration();
    }

    public @Nullable WorldConfiguration getDefault() {
        return defaultConfiguration;
    }

    public boolean isDefault(WorldConfiguration configuration) {
        return defaultConfiguration != null && defaultConfiguration.getName().equalsIgnoreCase(configuration.getName());
    }

    public boolean hasDefault() {
        return defaultConfiguration != null;
    }

    public int size() {
        return configurations.size();
    }

    public WorldConfiguration get(String name) {
        return configurations.get(name);
    }

    public boolean contains(String name) {
        return configurations.containsKey(name);
    }

    public @Nullable WorldConfiguration createNewConfiguration(String name, String author, World world) {
        assert ! contains(name) : "This configuration already exists";
        File file = new File(directory, name + ".yml");
        if(! file.exists()) {
            try {
                assert file.createNewFile();
            } catch(IOException e) {
                throw new RuntimeException("Could not create file " + file + ": ", e);
            }
        }

        WorldConfiguration configuration = new WorldConfiguration(file, name, author, world.getName());
        try {
            configuration.save();
        } catch(IOException e) {
            ReignOfCubes2.error("Could not save " + configuration + ": " + e);
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
