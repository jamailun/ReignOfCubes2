package fr.jamailun.reignofcubes2.api.configuration;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.configuration.sections.RocConfigurationSection;
import fr.jamailun.reignofcubes2.api.configuration.sections.RocConfigurationSectionsRegistry;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A possible configuration for a game.
 * <br/>
 * Specific to a world.
 */
public abstract class RocConfiguration {

    private final File file;
    protected final YamlConfiguration config;

    @Getter private final String name, author, worldName;
    protected final Map<Class<? extends RocConfigurationSection>, RocConfigurationSection> sections = new HashMap<>();

    /**
     * Load a WorldConfiguration from a file.
     * @param file the file to load.
     * @return a deserialized WorldConfiguration.
     * @throws BadConfigurationException if something is invalid.
     */
    public RocConfiguration(@NotNull File file) throws BadConfigurationException {
        this.file = file;
        if(!file.exists())
            throw new BadConfigurationException("File does not exist: " + file);
        config = YamlConfiguration.loadConfiguration(file);

        // Shared
        name = config.getString("name", "unknown");
        author = config.getString("author", "unknown");
        worldName = config.getString("world");
        if(worldName == null) throw new BadConfigurationException("World not specified in file " + file);
        if(Bukkit.getWorld(worldName) == null) throw new BadConfigurationException("World '"+worldName+"' does not exist. File " + file);

        // load sections
        reloadSections();

        // Conclusion
        ReignOfCubes2.logInfo("Loaded configuration " + this);
    }

    protected void reloadSections() {
        sections.clear();
        for(RocConfigurationSection section : RocConfigurationSectionsRegistry.generateSections(config)) {
            sections.put(section.getClass(), section);
        }
    }

    public RocConfiguration(File file, String name, String author, String worldName) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
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

        // Sections
        for(RocConfigurationSection section : sections.values()) {
            section.write(config);
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

    @SuppressWarnings("unchecked")
    public <T extends RocConfigurationSection> T getSection(Class<T> clazz) {
        return (T) sections.get(clazz);
    }

    public boolean isValid() {
        return sections.values().stream().allMatch(RocConfigurationSection::isValid);
    }

}
