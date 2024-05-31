package fr.jamailun.reignofcubes2.configuration.kits;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("KitItem")
@Getter
public class RocKitItem implements Cloneable, ConfigurationSerializable, Comparable<RocKitItem> {

    private int slot;
    private ItemStack item;

    private RocKitItem() {}

    public RocKitItem(int slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    public static RocKitItem deserialize(@NotNull Map<String, Object> map) {
        RocKitItem config = new RocKitItem();

        config.slot = (int) map.get("slot");
        config.item = (ItemStack) map.get("item");

        return config;
    }

    public void equip(PlayerInventory inventory) {
        inventory.setItem(slot, item);
    }

    public boolean isEquipment() {
        return slot >= 36 && slot <= 40;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("slot", slot);
        map.put("item", item);
        return map;
    }

    @Override
    public int compareTo(@NotNull RocKitItem o) {
        return slot - o.slot;
    }

    @Override
    public RocKitItem clone() {
        RocKitItem clone = new RocKitItem();
        clone.item = item.clone();
        clone.slot = slot;
        return clone;
    }
}
