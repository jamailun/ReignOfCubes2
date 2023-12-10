package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigurationsList {

    private final File directory;
    private WorldConfiguration defaultConfiguration;
    private final Map<String, WorldConfiguration> configurations = new HashMap<>();

    public ConfigurationsList() {
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
                ReignOfCubes2.error("Could not load " + configurations);
            }
        }
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

    }

    public @Nullable WorldConfiguration getDefault() {
        return defaultConfiguration;
    }

    public boolean isDefault(WorldConfiguration configuration) {
        return defaultConfiguration != null && defaultConfiguration.getName().equalsIgnoreCase(configuration.getName());
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
