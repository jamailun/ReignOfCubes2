package fr.jamailun.reignofcubes2.configuration.kits;

import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Kit extends Configurable {

    @Getter private String id;
    private String displayName;


    private Material iconType;
    @Getter private int cost;
    private final Set<KitItem> items = new HashSet<>();
    //Map<SlotDefinition, ItemConfiguration> items = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static Kit deserialize(@NotNull Map<String, Object> map) {
        Kit kit = new Kit();

        // Basic
        kit.id = (String) map.get("id");
        kit.displayName = (String) map.get("name");
        kit.cost = (int) map.get("cost");

        kit.iconType = loadMaterial("kit-"+kit.id, (String)map.get("icon-type"));
        if(kit.iconType == null)
            kit.iconType = Material.BARRIER;

        // Items
        List<Map<String, Object>> items = (List<Map<String,java.lang.Object>>) map.get("items");
        for(Map<String, Object> entry : items) {
            KitItem item = KitItem.deserialize(entry);
            if(item != null)
                kit.items.add(item);
        }
        return kit;
    }

    public void loadFromInventory(RocPlayer player) {
        PlayerInventory inventory = player.getPlayer().getInventory();
        items.clear();

        for(EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = inventory.getItem(slot);
            if(item.getType() != Material.AIR) {
                items.add(new KitItem(new SlotDefinition(slot), item));
            }
        }

        for(int slot = 0; slot < inventory.getMaxStackSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if(item != null) {
                items.add(new KitItem(new SlotDefinition(slot), item));
            }
        }
    }

    public int size() {
        return items.size();
    }

    public ItemStack toIcon() {
        return new ItemBuilder(iconType)
                .setName(displayName)
                .toItemStack();
    }

    public void equip(RocPlayer player) {
        PlayerInventory inventory = player.getPlayer().getInventory();
        inventory.clear();

        for(KitItem item : items) {
            item.equip(inventory);
        }
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return null;
    }
}
