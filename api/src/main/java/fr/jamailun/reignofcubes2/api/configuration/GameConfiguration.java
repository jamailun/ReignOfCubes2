package fr.jamailun.reignofcubes2.api.configuration;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.configuration.sections.RocConfigurationSection;
import fr.jamailun.reignofcubes2.api.configuration.sections.SectionsRegistry;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A possible configuration for a game.
 * <br/>
 * Specific to a world.
 */
public final class GameConfiguration {

    private final File file;
    @Getter private final String name, author, worldName;
    private final List<RocConfigurationSection> sections = new ArrayList<>();

    /**
     * Load a WorldConfiguration from a file.
     * @param file the file to load.
     * @return a deserialized WorldConfiguration.
     * @throws BadConfigurationException if something is invalid.
     */
    public static @NotNull GameConfiguration load(@NotNull File file) throws BadConfigurationException {
        if(!file.exists())
            throw new BadConfigurationException("File does not exist: " + file);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Shared
        String name = config.getString("name");
        String author = config.getString("author");
        String world = config.getString("world");
        if(name == null) name = "unknown";
        if(author == null) author = "unknown";
        if(world == null) throw new BadConfigurationException("World not specified in file " + file);
        if(Bukkit.getWorld(world) == null) throw new BadConfigurationException("World '"+world+"' does not exist. File " + file);
        GameConfiguration configuration = new GameConfiguration(file, name, author, world);

        // load sections
        configuration.sections.addAll(SectionsRegistry.generateSections(config));

        // Conclusion
        ReignOfCubes2.logInfo("Loaded configuration " + configuration);
        return configuration;
    }

    public GameConfiguration(File file, String name, String author, String worldName) {
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

        // Sections
        for(RocConfigurationSection section : sections) {
            section.write(config);
        }

        // Save
        config.save(file);
    }

}
