package fr.jamailun.reignofcubes2.api.configuration;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.configuration.sections.RocConfigurationSection;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A possible configuration for a game.
 * <br/>
 * Specific to a world.
 */
public abstract class RocConfiguration extends PropertiesHolder {

    private final File file;
    protected final YamlConfiguration config;

    @PersistedProperty(section = "metadata", name = "name")
    @Getter String name;
    @PersistedProperty(section = "metadata", name = "author")
    @Getter String author;
    @PersistedProperty(section = "metadata", name = "world-name")
    @Getter String worldName;

    protected final Map<String, PropertiesHolder> rulesHolder = new HashMap<>();

    /**
     * Load a WorldConfiguration from a file.
     * @param file the file to load.
     * @throws BadConfigurationException if something is invalid.
     */
    public RocConfiguration(@NotNull File file) throws BadConfigurationException {
        this.file = file;
        if(!file.exists())
            throw new BadConfigurationException("File does not exist: " + file);
        config = YamlConfiguration.loadConfiguration(file);

        // Load sections
        reloadHolders();

        // Read sections
        for(Map.Entry<String, PropertiesHolder> holder : rulesHolder.entrySet()) {
            ConfigurationSection section = config.createSection(holder.getKey());
            holder.getValue().read(section);
        }

        // Read metadata
        if(worldName == null) throw new BadConfigurationException("World not specified in file " + file);
        if(Bukkit.getWorld(worldName) == null) throw new BadConfigurationException("World '"+worldName+"' does not exist. File " + file);

        // Conclusion
        ReignOfCubes2.logInfo("Loaded configuration " + this);
    }

    /**
     * Clear and recreate all parts.
     */
    protected abstract void reloadHolders();

    public RocConfiguration(@NotNull File file, @NotNull String name, @NotNull String author, @NotNull String worldName) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
        this.name = name;
        this.author = author;
        this.worldName = worldName;
    }

    public void save() throws IOException {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Save parts
        for(Map.Entry<String, PropertiesHolder> holder : rulesHolder.entrySet()) {
            ConfigurationSection section = config.createSection(holder.getKey());
            holder.getValue().save(section);
        }

        // Save
        config.save(file);
    }

    /**
     * Fancy toString().
     * @return a Bukkit-colored String.
     */
    public final @NotNull String nicePrint() {
        String prefix = "§7{\n  author=§e"+author+"§7, world=§e"+worldName+"§7, name=\"§a" + name + "§7\",\n";
        StringBuilder sb = new StringBuilder(prefix);
        for(Map.Entry<Class<? extends RocConfigurationSection>, RocConfigurationSection> entry : sections.entrySet()) {
            String p = "  §b" + entry.getKey().getSimpleName().toLowerCase() + "§7 = {\n";
            String section = entry.getValue().nicePrint("    ", "  ");
            String s = "  §7,\n";
            sb.append(p).append(section).append(s);
        }
        return sb.append("§7}").toString();
    }

    @Contract(pure = true)
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    @Override
    public boolean isPlayable() {
        if(worldName == null || Bukkit.getWorld(worldName) == null)
            return false;
        return rulesHolder.values().stream().allMatch(PropertiesHolder::isPlayable);
    }

    public <T extends PropertiesHolder> T holder(@NotNull String name, @NotNull Class<T> holderClass) {
        return holderClass.cast(rulesHolder.get(name));
    }

    public abstract ItemStack getShopItem();

}
