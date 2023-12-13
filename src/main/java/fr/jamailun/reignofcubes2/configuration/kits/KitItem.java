package fr.jamailun.reignofcubes2.configuration.kits;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class KitItem extends Configurable implements Comparable<KitItem> {

    private SlotDefinition slot;
    @Getter private ItemStack item;

    private KitItem() {}

    public KitItem(SlotDefinition slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    @SuppressWarnings("unchecked")
    public static KitItem deserialize(@NotNull Map<String, Object> map) {
        KitItem config = new KitItem();

        config.slot = SlotDefinition.fromString((String) map.get("slot"));
        if(config.slot == null)
            return null;

        Map<String, Object> item = (Map<String, Object>) map.get("item");
        config.item = ItemStack.deserialize(item);

        return config;
    }

    public void equip(PlayerInventory inventory) {
        slot.equip(inventory, item);
    }

    public boolean isEquipment() {
        return slot.isEquipment();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "slot", slot.toString(),
                "item", item.serialize()
        );
    }

    @Override
    public int compareTo(@NotNull KitItem o) {
        return slot.compareTo(o.slot);
    }
}