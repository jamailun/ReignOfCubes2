package fr.jamailun.reignofcubes2.configuration.kits;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
public class KitItem extends Configurable implements Comparable<KitItem> {

    private int slot;
    private ItemStack item;

    private KitItem() {}

    public KitItem(int slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    @SuppressWarnings("unchecked")
    public static KitItem deserialize(@NotNull Map<String, Object> map) {
        KitItem config = new KitItem();

        config.slot = (int) map.get("slot");

        Map<String, Object> item = (Map<String, Object>) map.get("item");
        ReignOfCubes2.info("[debug] slot=" + config.slot+", map.get(item)=" + map.get("item"));
        config.item = ItemStack.deserialize(item);
        ReignOfCubes2.info("[debug] item=>" + config.item);

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
        return Map.of(
                "slot", slot,
                "item", item.serialize()
        );
    }

    public String slotString() {
        return String.valueOf(slot);
    }

    @Override
    public int compareTo(@NotNull KitItem o) {
        return slot - o.slot;
    }
}
