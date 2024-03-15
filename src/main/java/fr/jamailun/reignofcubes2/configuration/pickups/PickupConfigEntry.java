package fr.jamailun.reignofcubes2.configuration.pickups;

import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

/**
 * An immuable entry in configuration for a pickup.
 * @param id the ID of pickup in configuration. Must be unique per configuration.
 * @param material the material to use as an {@link org.bukkit.inventory.ItemStack}.
 * @param score the score given by the pickup. Must be positive.
 * @param chance the chance to summon it.
 * @param color the color to use for the firework.
 */
public record PickupConfigEntry(String id, Material material, int score, double chance, Color color) {

    /**
     * Deserialize a PickupConfigEntry from configuration.
     * @param id the ID of the entry.
     * @param section the ConfigurationSection to deserialize from.
     * @return a non-null and valid PickupConfigEntry.
     * @throws WorldConfiguration.BadWorldConfigurationException if the configuration is not valid.
     */
    public static @NotNull PickupConfigEntry deserialize(@NotNull String id, @NotNull ConfigurationSection section) throws WorldConfiguration.BadWorldConfigurationException {
        assertExists(id, section, "material");
        assertExists(id, section, "score");
        assertExists(id, section, "chance");
        assertExists(id, section, "color");

        // Deserialize
        String matStr = section.getString("material");
        Material material;
        try {
            material = Material.valueOf(matStr);
        } catch(NoSuchElementException e) {
            throw new WorldConfiguration.BadWorldConfigurationException("Invalid material for PickupConfigEntry "+id+": '"+matStr+"'.");
        }
        int score = section.getInt("score");
        if(score <= 0)
            throw new WorldConfiguration.BadWorldConfigurationException("Invalid non-positive score in PickupConfigEntry "+id+": "+score+".");
        double chance = section.getDouble("chance");
        Color color = Color.fromRGB(section.getInt("color"));
        return new PickupConfigEntry(id, material, score, chance, color);
    }

    /**
     * Serialize this entry to the configuration.
     * @param container the configuration holder to serialize to.
     */
    public void serialize(@NotNull ConfigurationSection container) {
        ConfigurationSection section = container.createSection(id);
        //
        section.set("material", material.name());
        section.set("score", score);
        section.set("chance", chance);
        section.set("color", color.asRGB());
    }

    private static void assertExists(String id, ConfigurationSection section, String key) throws WorldConfiguration.BadWorldConfigurationException {
        if(!section.contains(key))
            throw new WorldConfiguration.BadWorldConfigurationException("Missing key '"+key+"' in pickupEntry '" + id +"'.");
    }

}
