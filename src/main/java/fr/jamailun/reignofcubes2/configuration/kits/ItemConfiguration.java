package fr.jamailun.reignofcubes2.configuration.kits;

import fr.jamailun.reignofcubes2.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class ItemConfiguration extends Configurable {

    private String displayName;
    private Material type;

    @Getter private SlotDefinition slot;
    private int amount;
    private boolean unbreakable;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static ItemConfiguration deserialize(@NotNull Map<String, Object> map) {
        ItemConfiguration item = new ItemConfiguration();

        // basics
        item.displayName = (String) map.get("name");
        item.amount = (int) map.get("amount");
        item.slot = SlotDefinition.fromString((String) map.get("slot"));
        if(item.slot == null)
            return null;

        item.unbreakable = (boolean) map.get("unbreakable");

        // Material
        String type = (String) map.get("type");
        item.type = loadMaterial("item '"+ item.displayName+"'", type);
        if(item.type == null)
            return null;

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
