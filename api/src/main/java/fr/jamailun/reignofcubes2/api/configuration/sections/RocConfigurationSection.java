package fr.jamailun.reignofcubes2.api.configuration.sections;

import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class RocConfigurationSection {

    public abstract boolean isValid();

    public abstract String getSectionName();

    public abstract void write(@NotNull ConfigurationSection config);

    protected String niceInt(int num, int zero) {
        if(num == zero)
            return "§e" + num;
        return (num < zero ? "§c" : "§a") + num;
    }

    protected String niceDouble(double d, double zero) {
        if(d == zero)
            return "§e" + d;
        return (d < zero ? "§c" : "§a") + d;
    }

    public abstract String nicePrint(String prefix, String last);

    @SuppressWarnings("unchecked")
    protected static @NonNull List<Vector> getVectorsList(ConfigurationSection config, String path) {
        List<Vector> vectors = new ArrayList<>();
        List<Map<?, ?>> maps = config.getMapList(path);
        for(Map<?, ?> map : maps) {
            vectors.add(Vector.deserialize((Map<String, Object>) map));
        }
        return vectors;
    }

    protected static void setVectorsList(ConfigurationSection config, String path, @NonNull List<Vector> vectors) {
        List<Map<String, ?>> maps = new ArrayList<>();
        for(Vector v : vectors) {
            maps.add(v.serialize());
        }
        config.set(path, maps);
    }

}
