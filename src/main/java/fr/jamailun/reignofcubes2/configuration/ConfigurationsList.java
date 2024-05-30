package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.configuration.BadConfigurationException;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigurationsList {

    private final File directory;
    private GameConfiguration defaultConfiguration;
    private final Map<String, GameConfiguration> configurations = new HashMap<>();

    public ConfigurationsList() {
        // Load configurations
        directory = MainROC2.getFile("configurations");
        assert directory.exists() || (directory.mkdirs() && directory.mkdir()) : "Could not generate directories to '"+directory+"'";
        MainROC2.info("CONFIG_LIST = " + directory);

        File[] files = directory.listFiles();
        if(files == null || files.length == 0) {
            MainROC2.warning("No configuration exist at all.");
            return;
        }

        for(File file : files) {
            if( !isYaml(file)) continue;
            try {
                GameConfiguration configuration = new GameConfiguration(file);
                configurations.put(configuration.getName(), configuration);
            } catch (BadConfigurationException e) {
                MainROC2.error("Could not load file " + file + " : " + e.getMessage());
            }
        }

        // Load default
        ConfigurationSection config = MainROC2.getDefaultConfiguration();
        String defaultName = config.getString("default");
        if(defaultName == null) {
            MainROC2.warning("No default configuration set.");
            return;
        }
        GameConfiguration configuration = get(defaultName);
        if(configuration == null) {
            MainROC2.error("Unknown default configuration: '" + defaultName + "'.");
            return;
        }
        if(!configuration.isValid()) {
            MainROC2.error("Invalid default configuration: '" + defaultName + "'.");
            return;
        }
        defaultConfiguration = configuration;
    }

    public Map<String, GameConfiguration> getConfigurations() {
        return Collections.unmodifiableMap(configurations);
    }

    public List<GameConfiguration> list() {
        return configurations.values()
                .stream()
                .sorted(Comparator.comparing(GameConfiguration::getName))
                .toList();
    }

    public void setDefault(@Nullable GameConfiguration configuration) {
        // validity
        if(configuration != null) {
            if(!configuration.isValid()) {
                MainROC2.error("Cannot set "+ configuration.getName() + " has default configuration : it is invalid.'");
                return;
            }
        }
        // save
        defaultConfiguration = configuration;
        MainROC2.getDefaultConfiguration().set("default", configuration.getName());
        MainROC2.saveDefaultConfiguration();
    }

    public @Nullable GameConfiguration getDefault() {
        return defaultConfiguration;
    }

    public boolean isDefault(GameConfiguration configuration) {
        return defaultConfiguration != null && defaultConfiguration.getName().equalsIgnoreCase(configuration.getName());
    }

    public boolean hasDefault() {
        return defaultConfiguration != null;
    }

    public int size() {
        return configurations.size();
    }

    public GameConfiguration get(String name) {
        return configurations.get(name);
    }

    public boolean contains(String name) {
        return configurations.containsKey(name);
    }

    public @Nullable GameConfiguration createNewConfiguration(String name, String author, World world) {
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
            MainROC2.error("Could not save " + configuration + ": " + e);
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
