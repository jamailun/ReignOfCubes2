package fr.jamailun.reignofcubes2.configuration.kits;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class KitConfiguration implements ConfigurationSerializable {

    private String id, displayName;
    private int cost;
    private Map<String, ItemConfiguration> items;

    public static KitConfiguration deserialize(@NotNull Map<String, Object> map) {
        KitConfiguration kit = new KitConfiguration();

        // Basic
        kit.id = (String) map.get("id");
        kit.displayName = (String) map.get("name");
        kit.cost = (int) map.get("cost");

        // Items
        for(Map.Entry<?, ?> entry : map.entrySet()) {

        }
        return kit;
    }


    @Override
    public @NotNull Map<String, Object> serialize() {
        return null;
    }
}
