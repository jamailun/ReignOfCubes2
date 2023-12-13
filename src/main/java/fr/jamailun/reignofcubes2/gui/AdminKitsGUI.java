package fr.jamailun.reignofcubes2.gui;

import fr.jamailun.reignofcubes2.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.utils.ItemBuilder;
import fr.jamailun.reignofcubes2.utils.MenuGUI;
import org.bukkit.Material;

import java.util.List;

public class AdminKitsGUI extends MenuGUI {

    public AdminKitsGUI(RocPlayer player) {
        super(5, player, "gui.admin.kits.title");

        List<Kit> kits = kits().getKits();
        if(kits.isEmpty()) {
            set(22, new ItemBuilder(Material.RED_CONCRETE).setName("&cVide").toItemStack());
        } else {
            displayKits(kits);
        }

        setDefaultFooter();

        open();
    }

    private void displayKits(List<Kit> kits) {
        int slot = 0;
        for(Kit kit : kits) {
            set(slot, kit.toIcon(), () -> clickOnKit(kit));
            slot++;
        }
    }

    private void clickOnKit(Kit kit) {
        new AdminKitGUI(getPlayer(), kit);
    }
}
