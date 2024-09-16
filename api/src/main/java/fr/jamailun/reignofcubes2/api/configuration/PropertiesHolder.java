package fr.jamailun.reignofcubes2.api.configuration;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public abstract class PropertiesHolder implements Persistable {

    private final Map<String, PropertyObject> fields = new HashMap<>();

    public PropertiesHolder() {
        for(Field field : getClass().getDeclaredFields()) {
            PersistedProperty annotation = field.getAnnotation(PersistedProperty.class);
            if(annotation != null) {
                PropertyObject val = new PropertyObject(this, field, annotation);
                fields.put(val.path(), val);
            }
        }
    }

    public <T> T get(@NotNull String path, @NotNull Class<T> clazz) throws UnknownConfigurationException {
        PropertyObject entry = fields.get(path);
        if(entry == null) throw new UnknownConfigurationException(getClass().getSimpleName() + "::" + path);
        return entry.get(clazz);
    }
    public void set(@NotNull String path, @Nullable Object value) throws UnknownConfigurationException {
        PropertyObject entry = fields.get(path);
        if (entry == null) throw new UnknownConfigurationException(getClass().getSimpleName() + "::" + path);
        entry.set(value);
    }

    public @NotNull List<String> listPaths() {
        return List.copyOf(fields.keySet());
    }

    public @NotNull List<String> listPaths(@Nullable String section) {
        if(section == null) return listPaths();
        return fields.values().stream()
                .filter(o -> Objects.equals(section, o.section()))
                .map(PropertyObject::name)
                .toList();
    }

    public @NotNull List<String> listSections() {
        return fields.values().stream()
                .map(PropertyObject::section)
                .distinct()
                .toList();
    }

    public @NotNull List<PropertyObject> properties() {
        return List.copyOf(fields.values());
    }

    @Override
    public void read(@NotNull ConfigurationSection config) {
        for(PropertyObject val : properties()) {
            // Read section from config
            ConfigurationSection section = config.getConfigurationSection(val.section());
            if(section == null) continue;

            // Read value from section
            if(val.is(int.class) || val.is(Integer.class)) {
                val.set(section.getInt(val.name()));
            } else if(val.is(double.class) || val.is(Double.class)) {
                val.set(section.getDouble(val.name()));
            } else if(val.is(String.class)) {
                val.set(section.getString(val.name()));
            } else if(val.is(ItemStack.class)) {
                val.set(section.getItemStack(val.name()));
            } else if(val.is(Vector.class)) {
                val.set(section.getVector(val.name()));
            } else if(val.is(Location.class)) {
                val.set(section.getLocation(val.name()));
            } else {
                if(!readField(section, val)) {
                    throw new RuntimeException("(read) Invalid type for game-type-holder: " + val + " on type " + val.field().getType());
                }
            }
        }
    }

    @Override
    public void save(@NotNull ConfigurationSection config) {
        for(PropertyObject val : properties()) {
            // Get/create section from config
            ConfigurationSection section = config.getConfigurationSection(val.section());
            if(section == null) {
                section = config.createSection(val.section());
            }

            // Write value to section
            if(val.is(int.class) || val.is(Integer.class)) {
                section.set(val.name(), val.get(int.class));
            } else if(val.is(double.class) || val.is(Double.class)) {
                section.set(val.name(), val.get(double.class));
            } else if(val.is(String.class)) {
                section.set(val.name(), val.get(String.class));
            } else if(val.is(ItemStack.class)) {
                section.set(val.name(), val.get(ItemStack.class));
            } else if(val.is(Location.class)) {
                section.set(val.name(), val.get(Location.class));
            } else if(val.is(Vector.class)) {
                section.set(val.name(), val.get(Vector.class));
            } else {
                if(!saveField(section, val)) {
                    throw new RuntimeException("(save) Invalid type for game-type-holder: " + val + " on type " + val.field().getType());
                }
            }
        }
    }

    protected boolean saveField(@NotNull ConfigurationSection section, @NotNull PropertyObject property) {
        return false;
    }

    protected boolean readField(@NotNull ConfigurationSection section, @NotNull PropertyObject property) {
        return false;
    }

    @SuppressWarnings("unchecked")
    protected static @NonNull List<Vector> readVectorsList(@NotNull ConfigurationSection config, @NotNull String path) {
        List<Vector> vectors = new ArrayList<>();
        List<Map<?, ?>> maps = config.getMapList(path);
        for(Map<?, ?> map : maps) {
            vectors.add(Vector.deserialize((Map<String, Object>) map));
        }
        return vectors;
    }

    protected static void writeVectorsList(@NotNull ConfigurationSection config, @NotNull String path, @NotNull List<Vector> vectors) {
        List<Map<String, ?>> maps = new ArrayList<>();
        for(Vector v : vectors) {
            maps.add(v.serialize());
        }
        config.set(path, maps);
    }

}
