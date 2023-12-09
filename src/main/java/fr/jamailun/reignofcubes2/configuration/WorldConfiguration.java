package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.Throne;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorldConfiguration {

    private final File file;
    @Getter private final String name, author, worldName;
    @Getter @Setter private int playerCountMin = 0, playerCountMax = 0;
    private double crownningDuration = 0;
    private List<Vector> playerSpawns;
    private Vector throneA, throneB;

    public static WorldConfiguration load(String fileName) throws BadWorldConfigurationException {
        File file = new File(fileName);
        if(!file.exists())
            throw new BadWorldConfigurationException("File does not exist: " + file);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        String name = config.getString("name");
        String author = config.getString("author");
        String world = config.getString("world-name");
        if(name == null) name = "unknown";
        if(author == null) author = "unknown";
        if(world == null) throw new BadWorldConfigurationException("World not specified in file " + file);
        if(Bukkit.getWorld(world) == null) throw new BadWorldConfigurationException("World '"+world+"' does not exist. File " + file);
        WorldConfiguration configuration = new WorldConfiguration(file, name, author, world);

        // load throne
        ConfigurationSection throne = config.getConfigurationSection("throne");
        if(throne != null) {
            configuration.throneA = throne.getVector("pos_a");
            configuration.throneB = throne.getVector("pos_b");
        }

        // load player-counts
        ConfigurationSection playerCount = config.getConfigurationSection("players-count");
        if(playerCount != null) {
            configuration.playerCountMin = playerCount.getInt("min");
            configuration.playerCountMax = playerCount.getInt("max");
        }

        // Load spawns
        configuration.playerSpawns = getVectorsList(config, "spawns");

        configuration.crownningDuration = config.getDouble("crown-duration");

        return configuration;
    }

    private WorldConfiguration(File file, String name, String author, String worldName) {
        this.file = file;
        this.name = name;
        this.author = author;
        this.worldName = worldName;
    }

    public void save() throws IOException {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        // Basics
        config.set("name", name);
        config.set("author", author);
        config.set("world", worldName);

        // throne
        if(throneA != null && throneB != null) {
            ConfigurationSection th = config.createSection("throne");
            th.set("pos_a", throneA);
            th.set("pos_b", throneB);
        }

        // players-count
        if(playerCountMin > 0 && playerCountMax > 0) {
            ConfigurationSection pc = config.createSection("players-count");
            pc.set("min", playerCountMin);
            pc.set("max", playerCountMax);
        }

        // spawns
        if(playerSpawns != null)
            setVectorsList(config, "spawns", playerSpawns);

        if(crownningDuration > 0)
            config.set("crown-duration", crownningDuration);

        config.save(file);
    }

    public boolean isValid() {
        return (throneA != null && throneB != null)
                && (playerSpawns != null &&  !playerSpawns.isEmpty())
                && (playerCountMin > 0 && playerCountMax > playerCountMin)
                && (crownningDuration > 0);
    }

    public Throne generateThrone(GameManager game) {
        assert isValid() : "Can only generate a throne if the configuration is valid.";
        World world = Bukkit.getWorld(worldName);
        assert world != null;
        return new Throne(game, throneA, throneB, crownningDuration);
    }

    public List<Location> generateSpawns() {
        assert isValid() : "Can only generate spawns if the configuration is valid.";
        World world = Bukkit.getWorld(worldName);
        assert world != null;
        return playerSpawns.stream()
                .map(v -> v.toLocation(world))
                .toList();
    }

    public static class BadWorldConfigurationException extends Exception {
        public BadWorldConfigurationException(String msg) {
            super(msg);
        }
    }

    @SuppressWarnings("unchecked")
    private static @NonNull List<Vector> getVectorsList(ConfigurationSection config, String path) {
        List<Vector> vectors = new ArrayList<>();
        List<Map<?, ?>> maps = config.getMapList(path);
        for(Map<?, ?> map : maps) {
            vectors.add(Vector.deserialize((Map<String, Object>) map));
        }
        return vectors;
    }

    private static void setVectorsList(ConfigurationSection config, String path, @NonNull List<Vector> vectors) {
        List<Map<String, ?>> maps = new ArrayList<>();
        for(Vector v : vectors) {
            maps.add(v.serialize());
        }
        config.set(path, maps);
    }

}
