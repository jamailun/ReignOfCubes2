package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.configuration.BadConfigurationException;
import fr.jamailun.reignofcubes2.api.configuration.PropertiesHolder;
import fr.jamailun.reignofcubes2.api.configuration.RocConfiguration;
import fr.jamailun.reignofcubes2.api.configuration.UnknownConfigurationException;
import fr.jamailun.reignofcubes2.api.gameplay.Throne;
import fr.jamailun.reignofcubes2.api.utils.ParticlesHelper;
import fr.jamailun.reignofcubes2.gameplay.throne.ThroneImpl;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public GameConfiguration(@NotNull File file, @NotNull String name, @NotNull String author, @NotNull String worldName) {
        super(file, name, author, worldName);
        reloadHolders();
    }

    @Override
    protected void reloadHolders() {
        rulesHolder.clear();
        rulesHolder.put("metadata", this);
        rulesHolder.put(GameRules.NAME, new GameRules());
        rulesHolder.put(WorldDefinition.NAME, new WorldDefinition());
        rulesHolder.put("tags", new TagsHolder());
    }

    public <T> T getValue(@NotNull String path, @NotNull Class<T> clazz) throws UnknownConfigurationException {
        String[] primary = path.split("\\.", 2);
        PropertiesHolder holder = rulesHolder.get(primary[0]);
        if(holder == null) throw new UnknownConfigurationException(path);
        return holder.get(primary[1], clazz);
    }

    public void setValue(@NotNull String path, @Nullable Object value) throws UnknownConfigurationException {
        String[] primary = path.split("\\.", 2);
        PropertiesHolder holder = rulesHolder.get(primary[0]);
        if(holder == null) throw new UnknownConfigurationException(path);
        holder.set(primary[1], value);
    }

    public List<Vector> spawnsList() {
        return getWorldDefinition().getSpawns();
    }

    @Override
    public ItemStack getShopItem() {
        return new ItemStack(getWorldDefinition().getShopItem());
    }

    public Location lobby() {
        Vector lobby = getWorldDefinition().getLobby();
        if(lobby == null) return null;
        return lobby.toLocation(getWorld());
    }

    public final Debugger debug = new Debugger();

    public class Debugger {
        private final Map<UUID, BukkitTask> showing = new HashMap<>();
        public boolean toggle(@NotNull Player player) {
            UUID uuid = player.getUniqueId();
            if(showing.containsKey(uuid)) {
                showing.remove(uuid).cancel();
                return false;
            }
            WorldDefinition section = getWorldDefinition();
            BukkitTask task = MainROC2.runTaskTimer(() -> {
                // throne
                if(section.getThroneA() != null) {
                    Location a = section.getThroneA().toLocation(player.getWorld());
                    if(section.getThroneB() != null) {
                        Location b = section.getThroneB().toLocation(player.getWorld());
                        ParticlesHelper.playBox(player, a, b, 0.2, Particle.FLAME);
                    } else {
                        ParticlesHelper.playLine(player, a, a.clone().add(0, 0.1, 0), 0.1, Particle.ENCHANT);
                    }
                } else if(section.getThroneB() != null) {
                    Location b = section.getThroneB().toLocation(player.getWorld());
                    ParticlesHelper.playLine(player, b, b.clone().add(0, 0.1, 0), 0.1, Particle.ENCHANT);
                }

                // spawns
                for(Vector v : section.getSpawns()) {
                    Location l = v.toLocation(player.getWorld());
                    ParticlesHelper.playCircleXZ(player, l, 1, Math.toRadians(6), Particle.DRAGON_BREATH);
                }

                // Mines
                for(Vector v : section.getMines()) {
                    Location l = v.toLocation(player.getWorld()).add(0, 0.1, 0);
                    ParticlesHelper.playCircleXZ(player, l, 1, Math.toRadians(6), Particle.HEART);
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

    public @NotNull GameRules getRulesHolder() {
        return holder(GameRules.NAME, GameRules.class);
    }
    public @NotNull WorldDefinition getWorldDefinition() {
        return holder(WorldDefinition.NAME, WorldDefinition.class);
    }

}
