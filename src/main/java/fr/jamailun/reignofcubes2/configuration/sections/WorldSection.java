package fr.jamailun.reignofcubes2.configuration.sections;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.configuration.BadConfigurationException;
import fr.jamailun.reignofcubes2.api.configuration.sections.DeserializeConfiguration;
import fr.jamailun.reignofcubes2.api.configuration.sections.RocConfigurationSection;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WorldSection extends RocConfigurationSection {

    private List<Vector> spawns = new ArrayList<>();
    private List<Vector> generators = new ArrayList<>();
    @Setter private Vector throneA, throneB;
    @Setter private Vector lobby;
    @Setter private ItemStack shopItem;

    @DeserializeConfiguration
    public static @NotNull WorldSection load(@NotNull ConfigurationSection root) throws BadConfigurationException {
        ConfigurationSection config = root.createSection("world");
        WorldSection configuration = new WorldSection();

        // load throne
        ConfigurationSection throne = config.getConfigurationSection("throne");
        if(throne != null) {
            configuration.throneA = throne.getVector("pos_a");
            configuration.throneB = throne.getVector("pos_b");
        }

        // Load lobby
        configuration.lobby = config.getVector("lobby");

        // Load spawns
        configuration.spawns = getVectorsList(config, "spawns");
        configuration.generators = getVectorsList(config, "generators");

        // Shop item
        configuration.shopItem = config.getItemStack("shop-item");

        MainROC2.info("Loaded configuration " + configuration);
        return configuration;
    }

    @Override
    public void write(@NotNull ConfigurationSection root) {
        ConfigurationSection config = root.createSection("world");

        // throne
        if(throneA != null && throneB != null) {
            ConfigurationSection th = config.createSection("throne");
            th.set("pos_a", throneA);
            th.set("pos_b", throneB);
        }

        // Lobby
        if(lobby != null) {
            config.set("lobby", lobby);
        }

        // spawns
        if(spawns != null) {
            setVectorsList(config, "spawns", spawns);
        }

        // Generators
        if(generators != null) {
            setVectorsList(config, "generators", generators);
        }

        // Shop item
        config.set("shop-item", shopItem);
    }

    public boolean isValid() {
        return (throneA != null && throneB != null)
                && lobby != null
                && (!spawns.isEmpty())
                && shopItem != null;
    }

    public ItemStack getShopItem() {
        if(shopItem == null) return null;
        return new ItemStack(shopItem);
    }

    @Override
    public String nicePrint(String prefix, String last) {
        return "§7{"
                + prefix + "§7throne = " + niceVector(throneA) + " -> " +  niceVector(throneB)
                + prefix + "§7lobby = " + niceVector(lobby)
                + prefix + "§7spawns = " + niceInt(spawns.size(), 0)
                + prefix + "§7generators = " + niceInt(generators.size(), 0)
                + prefix + "§7shop-item = " + (shopItem == null ? "§cnone" : "§a"+shopItem.getType().name().toLowerCase())
                + last + "§7}";
    }


}
