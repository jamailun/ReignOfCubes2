package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.objects.Throne;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorldConfiguration {

    private final File file;
    @Getter private final String name, author, worldName;
    private List<Vector> spawns = new ArrayList<>();
    @Setter private Vector throneA, throneB;
    @Getter private GameRules rules;

    public static WorldConfiguration load(File file) throws BadWorldConfigurationException {
        if(!file.exists())
            throw new BadWorldConfigurationException("File does not exist: " + file);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        String name = config.getString("name");
        String author = config.getString("author");
        String world = config.getString("world");
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

        // Load spawns
        configuration.spawns = getVectorsList(config, "spawns");

        // Load rules
        ConfigurationSection rulesSection = config.getConfigurationSection("rules");
        configuration.rules = GameRules.load(rulesSection);

        ReignOfCubes2.info("Loaded configuration " + configuration);

        return configuration;
    }

    public WorldConfiguration(File file, String name, String author, String worldName) {
        this.file = file;
        this.name = name;
        this.author = author;
        this.worldName = worldName;
        this.rules = GameRules.defaultRules();
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

        // spawns
        if(spawns != null)
            setVectorsList(config, "spawns", spawns);

        // rules
        ConfigurationSection rulesSection = config.createSection("rules");
        rules.write(rulesSection);

        config.save(file);
    }

    public boolean isValid() {
        return (throneA != null && throneB != null)
                && (!spawns.isEmpty())
                && rules.isValid();
    }

    public Throne generateThrone(GameManager game) {
        assert isValid() : "Can only generate a throne if the configuration is valid.";
        World world = Bukkit.getWorld(worldName);
        assert world != null;
        return new Throne(game, throneA, throneB);
    }

    public List<Location> generateSpawns() {
        assert isValid() : "Can only generate spawns if the configuration is valid.";
        World world = Bukkit.getWorld(worldName);
        assert world != null;
        return spawns.stream()
                .map(v -> v.toLocation(world))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static class BadWorldConfigurationException extends Exception {
        public BadWorldConfigurationException(String msg) {
            super(msg);
        }
    }

    public List<Vector> spawnsList() {
        return spawns;
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

    @Override
    public String toString() {
        return "Configuration{" + name + " by " + author + " for " + worldName
                + (isValid() ? ", VALID": ", NOT-valid")
                + "}";
    }

    public String nicePrint() {
        String end = "\n  ";
        String endl = "§r,"+end;
        return "§r{" + end
                + "name = §6" + name + endl
                + "author = §6" + author + endl
                + "world = " + (Bukkit.getWorld(worldName) != null ? "§a" : "§c") + worldName + endl
                + "§l" + "valid = " + (isValid() ? "§atrue" : "§cfalse") + endl
                + "throne = " + niceVector(throneA) + " -> " +  niceVector(throneB) + endl
                + "spawns = §7" + Arrays.toString(spawns == null ? new Object[0] : spawns.toArray()) + endl
                + "rules = §7" + rules.nicePrint("\n    ", "\n  ")
                + "§r\n}";
    }

    private String niceVector(Vector vector) {
        return vector == null ? "§c<unset>§r" : "§a(" + vector.getX() + "," + vector.getY() + "," + vector.getZ() + ")§r";
    }

}
