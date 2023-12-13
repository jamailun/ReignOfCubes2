package fr.jamailun.reignofcubes2.gui;

import fr.jamailun.reignofcubes2.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.configuration.kits.KitItem;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.utils.ItemBuilder;
import fr.jamailun.reignofcubes2.utils.MenuGUI;
import org.bukkit.Material;

public class AdminKitGUI extends MenuGUI {

    public AdminKitGUI(RocPlayer player, Kit kit) {
        super(5, player, "Kit = " + kit.getDisplayName(), true);

        // Armor
        int slot = 1;
        set(0, new ItemBuilder(Material.OAK_SIGN).setName("Armure:").toItemStack());
        for(KitItem item : kit.listItems(true)) {
            set(slot, item.getItem(), () -> clickedOnItem(item));
            slot ++;
        }

        // Armor
        set(9, new ItemBuilder(Material.OAK_SIGN).setName("Objets:").toItemStack());
        slot = 10;
        for(KitItem item : kit.listItems(true)) {
            set(slot, item.getItem(), () -> clickedOnItem(item));
            slot ++;
        }

        // Footer
        setDefaultFooter();
        set(getSize()-9, BACK_ITEM, () -> new AdminKitsGUI(getPlayer()));

        open();
    }

    private void clickedOnItem(KitItem item) {

    }
}
