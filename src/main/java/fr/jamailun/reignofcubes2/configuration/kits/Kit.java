package fr.jamailun.reignofcubes2.configuration.kits;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.KitsConfiguration;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;


@SerializableAs("Kit")
public class Kit implements Cloneable, ConfigurationSerializable {

    @Setter private KitsConfiguration.KitsConfigurationSaver saver;
    @Getter private final String id;
    @Getter @Setter private String displayName;


    @Getter @Setter private Material iconType;
    @Getter @Setter private int cost;
    private final Set<KitItem> items = new HashSet<>();

    public Kit(String id) {
        this.id = id;
        iconType = Material.GRASS_BLOCK;
    }

    @SuppressWarnings("unchecked")
    public static Kit deserialize(@NotNull Map<String, Object> map) {
        // Id
        String id = (String) map.get("id");
        ReignOfCubes2.info("[Kit-debug] Kit id=" + id);

        // Create kit basics
        Kit kit = new Kit(id);
        kit.displayName = (String) map.get("name");
        kit.cost = (int) map.get("cost");

        // icon
        String iconType = (String)map.get("icon-type");
        try {
            kit.iconType = Material.valueOf(iconType);
        } catch(IllegalArgumentException e) {
            ReignOfCubes2.error("Invalid icon TYPE '" + iconType + "'.");
            kit.iconType = Material.BARRIER;
        }

        // Items
        List<KitItem> items = (List<KitItem>) map.get("items");
        assert items != null : "Null 'items' for Kit id="+id;
        kit.items.addAll(items);

        return kit;
    }

    public void loadFromInventory(RocPlayer player) {
        PlayerInventory inventory = player.getPlayer().getInventory();
        items.clear();

        for(int slot = 0; slot < inventory.getMaxStackSize(); slot++) {
            if(slot == 8) continue;
            ItemStack item = inventory.getItem(slot);
            if(item != null) {
                player.getPlayer().sendMessage("§fslot=§a"+slot+"§f, item=§e"+item.getType().name().toLowerCase());
                KitItem ki = new KitItem(slot, item);
                items.add(ki);

                if(ki.getItem().getType() == Material.AIR) {
                    ReignOfCubes2.error("[!] Item of slot " + ki.getSlot() + " has been corrupted in kit " + id);
                }
            }
        }
    }

    public void save() {
        assert saver != null;
        saver.save(this);
    }

    public int size() {
        return items.size();
    }

    public ItemStack toIcon() {
        return new ItemBuilder(iconType)
                .setName(displayName)
                .hideAll()
                .toItemStack();
    }

    public void equip(RocPlayer player) {
        PlayerInventory inventory = player.getPlayer().getInventory();
        inventory.clear();

        for(KitItem item : items) {
            item.equip(inventory);
            player.getPlayer().sendMessage("§f[>] slot=§e"+item.getSlot()+"§f, item=§a" + item.getItem().getType());
        }

        player.getPlayer().updateInventory();
    }

    public List<KitItem> listItems(boolean equipment) {
        return items.stream()
                .filter(i -> i.isEquipment() == equipment)
                .sorted(KitItem::compareTo)
                .toList();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("name", displayName);
        map.put("cost", cost);
        map.put("icon-type", iconType.name());
        map.put("items", List.copyOf(items));
        return map;
    }

    public void __debug(KitItem ki) {
        items.add(ki);
    }

    @Override
    public Kit clone() {
        Kit clone = new Kit(id);
        clone.saver = saver;
        clone.displayName = displayName;
        clone.cost = cost;
        clone.iconType = iconType;
        clone.items.addAll(items);
        return clone;
    }
}
