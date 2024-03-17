package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.pickups.PickupConfiguration;
import fr.jamailun.reignofcubes2.objects.Throne;
import fr.jamailun.reignofcubes2.utils.ParticlesPlayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A possible configuration for a game.
 */
public class WorldConfiguration {

    private final File file;
    @Getter private final String name, author, worldName;
    private List<Vector> spawns = new ArrayList<>();
    private List<Vector> generators = new ArrayList<>();
    @Setter private Vector throneA, throneB;
    @Setter private Location lobby;
    @Getter private GameRules rules;
    @Getter private TagsConfiguration tagsConfiguration = new TagsConfiguration();
    @Setter private ItemStack shopItem;
    @Getter private final PickupConfiguration pickupConfiguration;

    /**
     * Load a WorldConfiguration from a file.
     * @param file the file to load.
     * @return a deserialized WorldConfiguration.
     * @throws BadWorldConfigurationException if something is invalid.
     */
    public static @NotNull WorldConfiguration load(@NotNull File file) throws BadWorldConfigurationException {
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

        // Load lobby
        configuration.lobby = config.getLocation("lobby");

        // Load spawns
        configuration.spawns = getVectorsList(config, "spawns");
        configuration.generators = getVectorsList(config, "generators");

        // Load rules
        ConfigurationSection rulesSection = config.getConfigurationSection("rules");
        configuration.rules = GameRules.load(rulesSection);

        // Shop item
        configuration.shopItem = config.getItemStack("shop-item");

        // Pickups
        ConfigurationSection pickups = config.getConfigurationSection("pickups");
        if(pickups != null) {
            configuration.pickupConfiguration.deserialize(pickups);
        }

        // Tags
        ConfigurationSection tags = config.getConfigurationSection("tags");
        if(tags != null) {
            configuration.tagsConfiguration = TagsConfiguration.load(tags);
        }

        ReignOfCubes2.info("Loaded configuration " + configuration);
        return configuration;
    }

    public WorldConfiguration(File file, String name, String author, String worldName) {
        this.file = file;
        this.name = name;
        this.author = author;
        this.worldName = worldName;
        this.rules = GameRules.defaultRules();
        pickupConfiguration = new PickupConfiguration();
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

        // Lobby
        if(lobby != null) {
            config.set("lobby", lobby);
        }

        // spawns
        if(spawns != null) {
            setVectorsList(config, "spawns", spawns);
        }
        // Generators
        if(generators != null) {
            setVectorsList(config, "generators", generators);
        }

        // rules
        ConfigurationSection rulesSection = config.createSection("rules");
        rules.write(rulesSection);

        // Shop item
        config.set("shop-item", shopItem);

        // Pickups
        ConfigurationSection pickups = config.createSection("pickups");
        pickupConfiguration.save(pickups);

        // Tags
        ConfigurationSection tags = config.createSection("tags");
        tagsConfiguration.write(tags);

        //
        config.save(file);
    }

    public boolean isValid() {
        return (throneA != null && throneB != null)
                && lobby != null
                && (!spawns.isEmpty())
                && shopItem != null
                && rules.isValid()
                && ! pickupConfiguration.isEmpty()
                && tagsConfiguration.isValid();
    }

    public Throne generateThrone(GameManager game) {
        assert isValid() : "Can only generate a throne if the configuration is valid.";
        World world = Bukkit.getWorld(worldName);
        assert world != null;
        return new Throne(game, throneA, throneB);
    }

    public Location getLobby() {
        assert lobby != null;
        return lobby;
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

    public List<Location> getGeneratorsList(World world) {
        if(generators == null)
            return Collections.emptyList();
        return generators.stream()
                .map(v -> v.toLocation(world))
                .toList();
    }

    public List<Vector> listGenerators() {
        return generators;
    }

    public ItemStack getShopItem() {
        if(shopItem == null) return null;
        return new ItemStack(shopItem);
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

    /**
     * Fancy toString().
     * @return a Bukkit-colored String.
     */
    public String nicePrint() {
        String end = "\n  ";
        String endl = "§r,"+end;
        return "§r{" + end
                + "name = §6" + name + "§r, author = §6" + author + endl
                + "world = " + (Bukkit.getWorld(worldName) != null ? "§a" : "§c") + worldName + endl
                + "§lvalid = " + (isValid() ? "§atrue" : "§cfalse") + endl
                + "throne = " + niceVector(throneA) + " -> " +  niceVector(throneB)
                    + "§r, lobby = " + (lobby==null?"§4null":niceVector(lobby.toVector())+"/"+lobby.getWorld().getName()) + endl
                + "spawns = " + (spawns.isEmpty()?"§c":"§a") + spawns.size()
                    + "§r, generators = " + (generators.isEmpty()?"§c":"§a") + generators + endl
                + "pickups = " + (pickupConfiguration.isEmpty()?"§c":"§a") + pickupConfiguration + endl
                + "shop-item = " + (shopItem == null ? "§cnone" : "§a"+shopItem.getType()) + endl
                + "rules = §7" + rules.nicePrint("\n    ", "\n  ")
                + "§r, tags = " + tagsConfiguration.nicePrint("\n    ", "\n  ")
                + "§r\n}";
    }

    private String niceVector(Vector vector) {
        return vector == null ? "§c<unset>§r" : "§a(" + vector.getX() + "," + vector.getY() + "," + vector.getZ() + ")§r";
    }

    public Location getSafeSpawn(boolean trySafe) {
        // Get spawns, and shuffle
        List<Location> locations = generateSpawns();
        Collections.shuffle(locations);

        // Iterate over spawns, check safety
        if(trySafe) {
            for(Location spawn : locations) {
                if(isSafe(spawn)) {
                    return spawn;
                }
            }
        }

        return locations.get(0);
    }

    private boolean isSafe(Location location) {
        World world = Bukkit.getWorld(worldName);
        assert world != null;
        return world.getNearbyPlayers(location, 10).isEmpty();
    }

    public final Debugger debug = new Debugger();

    public class Debugger {
        private final Map<UUID, BukkitTask> showing = new HashMap<>();
        public boolean toggle(Player player) {
            UUID uuid = player.getUniqueId();
            if(showing.containsKey(uuid)) {
                showing.remove(uuid).cancel();
                return false;
            }
            BukkitTask task = ReignOfCubes2.runTaskTimer(() -> {
                // throne
                if(throneA != null) {
                    Location a = throneA.toLocation(player.getWorld());
                    if(throneB != null) {
                        Location b = throneB.toLocation(player.getWorld());
                        ParticlesPlayer.playBox(player, a, b, 0.2, Particle.FLAME);
                    } else {
                        ParticlesPlayer.playLine(player, a, a.clone().add(0, 0.1, 0), 0.1, Particle.ENCHANTMENT_TABLE);
                    }
                } else if(throneB != null) {
                    Location b = throneB.toLocation(player.getWorld());
                    ParticlesPlayer.playLine(player, b, b.clone().add(0, 0.1, 0), 0.1, Particle.ENCHANTMENT_TABLE);
                }

                // spawns
                for(Vector v : spawns) {
                    Location l = v.toLocation(player.getWorld());
                    ParticlesPlayer.playCircleXZ(player, l, 1, Math.toRadians(6), Particle.DRAGON_BREATH);
                }

                // Generators
                for(Vector v : generators) {
                    Location l = v.toLocation(player.getWorld()).add(0, 0.1, 0);
                    ParticlesPlayer.playCircleXZ(player, l, 1, Math.toRadians(6), Particle.TOTEM);
                }

                //lobby
                if(lobby != null) {
                    Location l = lobby.toLocation(player.getWorld());
                    ParticlesPlayer.playCircleXZ(player, l, 2, Math.toRadians(12), Particle.ELECTRIC_SPARK);
                }
            },1);
            showing.put(uuid, task);
            return true;
        }
    }

}
