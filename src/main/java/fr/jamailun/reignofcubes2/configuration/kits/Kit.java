package fr.jamailun.reignofcubes2.configuration.kits;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.tags.Tag;
import fr.jamailun.reignofcubes2.tags.TagsRegistry;
import fr.jamailun.reignofcubes2.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Kit {

    @Getter private final String id;
    @Getter @Setter private String displayName;
    @Getter @Setter private Material iconType;
    @Getter @Setter private int cost;

    @Getter private Tag tag;
    @Getter private String tagId; //used when 'tag' is null.

    private final Set<KitItem> items = new HashSet<>();

    private final File file;
    private final YamlConfiguration config;

    public Kit(File file) {
        this.file = file;
        if(!file.exists()) {
            throw new RuntimeException("Kit file must exist ! " + file);
        }
        id = file.getName().replace(".yml", "");
        ReignOfCubes2.info("Kit reading file " + file + ", ID is '" + id + "'.");
        config = YamlConfiguration.loadConfiguration(file);
        reload();
    }

    public Kit(File folder, String id) {
        file = new File(folder, id + ".yml");
        this.id = id;
        // create
        try {
            if(!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Could not create KIT file " + file, e);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        if(!(assertContains("name") && assertContains("cost") && assertContains("icon-type") && assertContains("items")))
            return;

        // Create kit basics
        displayName = config.getString("name");
        cost = config.getInt("cost");

        // icon
        String iconTypeStr = config.getString("icon-type");
        try {
            iconType = Material.valueOf(iconTypeStr);
        } catch(IllegalArgumentException e) {
            ReignOfCubes2.error("Invalid icon TYPE '" + iconTypeStr + "'.");
            iconType = Material.BARRIER;
        }

        // tag
        tagId = config.getString("tag");
        tag = TagsRegistry.find(tagId);

        // Items
        items.clear();
        List<?> itemsList = config.getList("items");
        if(itemsList == null) {
            ReignOfCubes2.error("No 'items' list in kit-file.");
            return;
        }
        for(Object entry : itemsList) {
            if(entry instanceof KitItem item) {
                items.add(item);
            } else {
                ReignOfCubes2.error("Unknown item type : " + entry + " | " + entry.getClass());
            }
        }
    }

    private boolean assertContains(String key) {
        if(!config.contains(key)) {
            ReignOfCubes2.error("Kit " + file + " does NOT contains key '" + key +"'.");
            return false;
        }
        return true;
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
        try {
            config.set("name", displayName);
            config.set("cost", cost);
            config.set("icon-type", iconType.name());
            config.set("tag", tagId);
            config.set("items", items.stream().sorted(Comparator.comparing(KitItem::getSlot)).toList());

            config.save(file);
        } catch (IOException e) {
            ReignOfCubes2.error("Could not save kit '" + id + "' : " + e.getMessage());
        }
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
        Player p = player.getPlayer();
        PlayerInventory inventory = p.getInventory();
        inventory.clear();

        // Add all items
        for(KitItem item : items) {
            item.equip(inventory);
            //player.getPlayer().sendMessage("§f[>] slot=§e"+item.getSlot()+"§f, item=§a" + item.getItem().getType());
        }

        // if in game ? add shop item !
        if(ReignOfCubes2.isPlaying()) {
            ItemStack is = ReignOfCubes2.getCurrentConfig().getShopItem();
            if (is != null) {
                p.getInventory().setItem(8, is);
            }
        }

        p.updateInventory();

        player.setTag(tag);
    }

    public List<KitItem> listItems(boolean equipment) {
        return items.stream()
                .filter(i -> i.isEquipment() == equipment)
                .sorted(KitItem::compareTo)
                .toList();
    }

    public void archiveFile(File folder) {
        File newFile = new File(folder, id + "." + UUID.randomUUID().toString().substring(20) + ".removed");
        if( ! file.renameTo(newFile)) {
            ReignOfCubes2.error("Could not rename kit file " + file + " to " + newFile);
        }
    }

    public void setTagId(@NotNull String tagId) {
        this.tagId = tagId;
        this.tag = TagsRegistry.find(tagId);
    }
}
