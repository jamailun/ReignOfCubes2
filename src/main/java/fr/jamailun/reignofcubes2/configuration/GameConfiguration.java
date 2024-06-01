package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.GameManagerImpl;
import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.configuration.BadConfigurationException;
import fr.jamailun.reignofcubes2.api.configuration.RocConfiguration;
import fr.jamailun.reignofcubes2.api.gameplay.Throne;
import fr.jamailun.reignofcubes2.api.utils.ParticlesHelper;
import fr.jamailun.reignofcubes2.configuration.pickups.PickupConfigurationSection;
import fr.jamailun.reignofcubes2.configuration.sections.GameRulesSection;
import fr.jamailun.reignofcubes2.configuration.sections.TagsConfigurationSection;
import fr.jamailun.reignofcubes2.configuration.sections.WorldSection;
import fr.jamailun.reignofcubes2.gameplay.ThroneImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A possible configuration for a game.
 */
public class GameConfiguration extends RocConfiguration {

    public GameConfiguration(@NotNull File file) throws BadConfigurationException {
        super(file);
    }

    public GameConfiguration(File file, String name, String author, String worldName) {
        super(file, name, author, worldName);
        reloadSections();
    }

    public TagsConfigurationSection getTagsSection() {
        return getSection(TagsConfigurationSection.class);
    }

    public GameRulesSection getRules() {
        return getSection(GameRulesSection.class);
    }

    public PickupConfigurationSection getPickupsSection() {
        return getSection(PickupConfigurationSection.class);
    }

    public WorldSection getWorldSection() {
        return getSection(WorldSection.class);
    }

    private World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    public Throne generateThrone(GameManagerImpl game) {
        if(!isValid())
            throw new RuntimeException("Can only generate a throne if the configuration is valid.");
        if(getWorld() == null)
            throw new RuntimeException("Invalid world: "+getWorldName()+".");
        WorldSection worldSection = getSection(WorldSection.class);
        return new ThroneImpl(game, worldSection.getThroneA(), worldSection.getThroneB());
    }

    public Location getLobby() {
        WorldSection worldSection = getSection(WorldSection.class);
        return worldSection.getLobby().toLocation(getWorld());
    }

    public List<Location> generateSpawns() {
        assert isValid() : "Can only generate spawns if the configuration is valid.";
        return getSection(WorldSection.class).getSpawns().stream()
                .map(v -> v.toLocation(getWorld()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Vector> spawnsList() {
        return getSection(WorldSection.class).getSpawns();
    }

    public List<Location> getGeneratorsList(World world) {
        return getSection(WorldSection.class).getGenerators().stream()
                .map(v -> v.toLocation(world))
                .toList();
    }

    public List<Vector> listGenerators() {
        return getSection(WorldSection.class).getGenerators();
    }

    public ItemStack getShopItem() {
        return getSection(WorldSection.class).getShopItem();
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
        return getWorld().getNearbyPlayers(location, 10).isEmpty();
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
            WorldSection section = getSection(WorldSection.class);
            BukkitTask task = MainROC2.runTaskTimer(() -> {
                // throne
                if(section.getThroneA() != null) {
                    Location a = section.getThroneA().toLocation(player.getWorld());
                    if(section.getThroneB() != null) {
                        Location b = section.getThroneB().toLocation(player.getWorld());
                        ParticlesHelper.playBox(player, a, b, 0.2, Particle.FLAME);
                    } else {
                        ParticlesHelper.playLine(player, a, a.clone().add(0, 0.1, 0), 0.1, Particle.ENCHANTMENT_TABLE);
                    }
                } else if(section.getThroneB() != null) {
                    Location b = section.getThroneB().toLocation(player.getWorld());
                    ParticlesHelper.playLine(player, b, b.clone().add(0, 0.1, 0), 0.1, Particle.ENCHANTMENT_TABLE);
                }

                // spawns
                for(Vector v : section.getSpawns()) {
                    Location l = v.toLocation(player.getWorld());
                    ParticlesHelper.playCircleXZ(player, l, 1, Math.toRadians(6), Particle.DRAGON_BREATH);
                }

                // Generators
                for(Vector v : section.getGenerators()) {
                    Location l = v.toLocation(player.getWorld()).add(0, 0.1, 0);
                    ParticlesHelper.playCircleXZ(player, l, 1, Math.toRadians(6), Particle.TOTEM);
                }

                //lobby
                if(section.getLobby() != null) {
                    Location l = section.getLobby().toLocation(player.getWorld());
                    ParticlesHelper.playCircleXZ(player, l, 2, Math.toRadians(12), Particle.ELECTRIC_SPARK);
                }
            },1);
            showing.put(uuid, task);
            return true;
        }
    }

}
