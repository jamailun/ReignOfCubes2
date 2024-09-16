package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.api.configuration.PersistedProperty;
import fr.jamailun.reignofcubes2.api.configuration.PropertiesHolder;
import fr.jamailun.reignofcubes2.api.configuration.PropertyObject;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class WorldDefinition extends PropertiesHolder {

    @PersistedProperty(section = "objects", name = "spawns", type = Vector.class)
    List<Vector> spawns = new ArrayList<>();

    @PersistedProperty(section = "objects", name = "mines", type = Vector.class)
    List<Vector> mines = new ArrayList<>();

    @PersistedProperty(section = "objects", name = "lobby")
    Vector lobby;

    @PersistedProperty(section = "objects", name = "throne.min")
    Vector throneA;
    @PersistedProperty(section = "objects", name = "throne.max")
    Vector throneB;

    @PersistedProperty(section = "shop", name = "item")
    ItemStack shopItem;

    @Override
    protected boolean readField(@NotNull ConfigurationSection section, @NotNull PropertyObject property) {
        if(property.isListOf(Vector.class)) {
            readVectorsList(section, property.section());
            return true;
        }
        return false;
    }

    @Override
    public boolean isPlayable() {
        return (throneA != null && throneB != null)
                && lobby != null
                && shopItem != null
                && !spawns.isEmpty()
                ;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean saveField(@NotNull ConfigurationSection section, @NotNull PropertyObject property) {
        if(property.isListOf(Vector.class)) {
            writeVectorsList(section, property.section(), property.get(List.class));
            return true;
        }
        return false;
    }
}
