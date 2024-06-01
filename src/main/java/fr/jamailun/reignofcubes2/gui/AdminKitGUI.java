package fr.jamailun.reignofcubes2.gui;

import fr.jamailun.reignofcubes2.api.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.utils.ItemBuilder;
import fr.jamailun.reignofcubes2.utils.MenuGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AdminKitGUI extends MenuGUI {

    public AdminKitGUI(RocPlayer player, Kit kit) {
        super(6, player, "Kit = " + kit.getDisplayName(), true);

        // Armor
        int slot = 1;
        set(0, new ItemBuilder(Material.OAK_SIGN).setName("§bArmure:").toItemStack());
        for(ItemStack item : kit.listItems(true)) {
            ItemBuilder ib = new ItemBuilder(item);
            set(slot, ib.toItemStack());
            slot ++;
        }

        // Armor
        set(9, new ItemBuilder(Material.OAK_SIGN).setName("§bObjets:").toItemStack());
        slot = 10;
        for(ItemStack item : kit.listItems(false)) {
            ItemStack is = new ItemStack(item);
            set(slot, is);
            slot ++;
        }

        // Footer
        setDefaultFooter();
        set(getSize() - 9, BACK_ITEM, () -> new AdminKitsGUI(getPlayer()));
        set(getSize() - 7,
                new ItemBuilder(Material.GOLD_BLOCK).setName("&aGive kit").toItemStack(),
                () -> {
                    kit.equip(player);
                    player.getPlayer().closeInventory();
                }
        );

        open();
    }

}
