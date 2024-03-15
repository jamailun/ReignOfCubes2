package fr.jamailun.reignofcubes2.gui;

import fr.jamailun.reignofcubes2.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.configuration.kits.KitItem;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.utils.ItemBuilder;
import fr.jamailun.reignofcubes2.utils.MenuGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AdminKitGUI extends MenuGUI {

    public AdminKitGUI(RocPlayer player, Kit kit) {
        super(6, player, "Kit = " + kit.getDisplayName(), true);

        // Armor
        int slot = 1;
        set(0, new ItemBuilder(Material.OAK_SIGN).setName("§bArmure:").toItemStack());
        for(KitItem item : kit.listItems(true)) {
            ItemBuilder ib = new ItemBuilder(item.getItem());
            //if(item.getSlot() == 40) ib.addLoreLine("§4Slot: §eOFF_HAND");
            set(slot, ib.toItemStack());
            slot ++;
        }

        // Armor
        set(9, new ItemBuilder(Material.OAK_SIGN).setName("§bObjets:").toItemStack());
        slot = 10;
        for(KitItem item : kit.listItems(false)) {
            ItemStack is = new ItemBuilder(item.getItem()).toItemStack();//.addLoreLine("§dSlot: §e"+item.slotString()).toItemStack();
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
