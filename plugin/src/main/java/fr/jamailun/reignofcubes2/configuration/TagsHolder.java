package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.api.configuration.PropertiesHolder;
import fr.jamailun.reignofcubes2.api.tags.RocTag;
import fr.jamailun.reignofcubes2.api.tags.TagsRegistry;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class TagsHolder extends PropertiesHolder {

    private final Map<String, RocTag> tags = new HashMap<>();

    public TagsHolder() {
        for(RocTag tag : TagsRegistry.list()) {
            tags.put(tag.getId(), tag);
        }
    }

    @Override
    public void read(@NotNull ConfigurationSection config) {
        for(Map.Entry<String, RocTag> tag : tags.entrySet()) {
            ConfigurationSection section = config.getConfigurationSection(tag.getKey());
            if(section != null) {
                tag.getValue().read(section);
            }
        }
    }

    @Override
    public void save(@NotNull ConfigurationSection config) {
        for(Map.Entry<String, RocTag> tag : tags.entrySet()) {
            ConfigurationSection section = config.createSection(tag.getKey());
            tag.getValue().save(section);
        }
    }

    @Override
    public boolean isPlayable() {
        return tags.values().stream().allMatch(RocTag::isPlayable);
    }
}
