package fr.jamailun.reignofcubes2.configuration.kits;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
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
public class KitItem implements Cloneable, ConfigurationSerializable, Comparable<KitItem> {

    private int slot;
    private ItemStack item;

    private KitItem() {}

    public KitItem(int slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    public static KitItem deserialize(@NotNull Map<String, Object> map) {
        KitItem config = new KitItem();

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

    public String slotString() {
        return String.valueOf(slot);
    }

    @Override
    public int compareTo(@NotNull KitItem o) {
        return slot - o.slot;
    }

    @Override
    public KitItem clone() {
        KitItem clone = new KitItem();
        clone.item = item.clone();
        clone.slot = slot;
        return clone;
    }
}
