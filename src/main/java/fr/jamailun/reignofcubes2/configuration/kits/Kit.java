package fr.jamailun.reignofcubes2.configuration.kits;

import fr.jamailun.reignofcubes2.configuration.KitsConfiguration;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class Kit extends Configurable {

    private final KitsConfiguration.KitsConfigurationSaver saver;
    @Getter private final String id;
    @Getter @Setter private String displayName;


    @Getter @Setter private Material iconType;
    @Getter @Setter private int cost;
    private final Set<KitItem> items = new HashSet<>();

    public Kit(KitsConfiguration.KitsConfigurationSaver saver, String id) {
        this.id = id;
        this.saver = saver;
    }

    @SuppressWarnings("unchecked")
    public static Kit deserialize(@NotNull Map<String, Object> map, KitsConfiguration.KitsConfigurationSaver saver) {
        // Id
        String id = (String) map.get("id");

        // Create kit basics
        Kit kit = new Kit(saver, id);
        kit.displayName = (String) map.get("name");
        kit.cost = (int) map.get("cost");

        // icon
        kit.iconType = loadMaterial("kit-"+kit.id, (String)map.get("icon-type"));
        if(kit.iconType == null)
            kit.iconType = Material.BARRIER;

        // Items
        List<Map<String, Object>> items = (List<Map<String,java.lang.Object>>) map.get("items");
        for(Map<String, Object> entry : items) {
            KitItem item = KitItem.deserialize(entry);
            kit.items.add(item);
        }
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
                items.add(new KitItem(slot, item));
            }
        }
    }

    public void save() {
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
        return Map.of(
                "id", id,
                "name", displayName,
                "cost", cost,
                "icon-type", iconType.name(),
                "items", items.stream().map(KitItem::serialize).toList()
        );
    }
}
