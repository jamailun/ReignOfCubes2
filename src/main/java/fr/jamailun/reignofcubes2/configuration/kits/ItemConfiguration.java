package fr.jamailun.reignofcubes2.configuration.kits;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemConfiguration implements ConfigurationSerializable {

    private String displayName;
    private Material type;
    private int amount, slot;
    private boolean unbreakable;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static ItemConfiguration deserialize(@NotNull Map<String, Object> map) {
        ItemConfiguration item = new ItemConfiguration();

        // basics
        item.displayName = (String) map.get("name");
        item.amount = (int) map.get("amount");
        item.slot = (int) map.get("slot");
        item.unbreakable = (boolean) map.get("unbreakable");

        // Material
        try {
            item.type = Material.valueOf((String) map.get("type"));
        } catch(IllegalArgumentException e) {
            ReignOfCubes2.error("Invalid TYPE for item '"+item.displayName+"': '" + map.get("type") + "'.");
            item.type = Material.BARRIER;
        }

        // Enchants
        List<String> enchants = (List<String>) map.get("enchantments");
        for(String enchant : enchants) {
            Map.Entry<Enchantment, Integer> entry = loadEnchant("item '"+ item.displayName+"'", enchant);
            if(entry != null) {
                item.enchantments.put(entry.getKey(), entry.getValue());
            }
        }

        return item;
    }

    private static Map.Entry<Enchantment, Integer> loadEnchant(String owner, String input) {
        String[] tokens = input.split(";");
        if(tokens.length != 2) {
            ReignOfCubes2.error("Invalid enchant ENTRY for "+owner+": '" + input + "'.");
            return null;
        }
        Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(tokens[0]));
        if(enchant == null) {
            ReignOfCubes2.error("Invalid enchant NAME for "+owner+": '" + tokens[0] + "'.");
            return null;
        }
        int level;
        try {
            level = Integer.parseInt(tokens[1]);
            if(level < 0) throw new NumberFormatException();
        } catch(NumberFormatException ignored) {
            ReignOfCubes2.error("Invalid enchant LEVEL for "+owner+": '" + tokens[1] + "'.");
            return null;
        }
        return Map.entry(enchant, level);
    }

    public ItemStack toItem() {
        return new ItemBuilder(type, amount)
                .setName(displayName)
                .setUnbreakable(unbreakable)
                .addUnsafeEnchantments(enchantments)
                .toItemStack();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "name", displayName,
                "amount", amount,
                "slot", slot,
                "unbreakable", unbreakable,
                "type", type.name(),
                "enchantments", enchantments.entrySet().stream()
                        .map(e -> e.getKey().getKey().getKey() + ";" + e.getValue())
                        .toList()
        );
    }
}
