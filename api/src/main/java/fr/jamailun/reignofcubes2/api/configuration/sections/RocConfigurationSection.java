package fr.jamailun.reignofcubes2.api.configuration.sections;

import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A section in the ROC configuration.
 */
public abstract class RocConfigurationSection {

    /**
     * Test if this rules-set is valid.
     * @return false if the configuration cannot be played.
     */
    public abstract boolean isValid();

    /**
     * Serialize this GameRules to configuration.
     * @param config the configuration to write to.
     */
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

    /**
     * Create a string for this object.
     * @param prefix prefix of each line (indent)
     * @param last suffix (indent-1).
     * @return a bukkit-colored String.
     */
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

    protected String niceVector(Vector vector) {
        return vector == null ? "§c<unset>§r" : "§a(" + vector.getX() + "," + vector.getY() + "," + vector.getZ() + ")§r";
    }


}
