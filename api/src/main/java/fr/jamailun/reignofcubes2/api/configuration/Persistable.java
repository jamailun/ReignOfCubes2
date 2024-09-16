package fr.jamailun.reignofcubes2.api.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public interface Persistable {

    void read(@NotNull ConfigurationSection section);

    void save(@NotNull ConfigurationSection section);

    boolean isPlayable();


}
