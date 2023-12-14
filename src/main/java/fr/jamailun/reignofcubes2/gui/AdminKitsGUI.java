package fr.jamailun.reignofcubes2.gui;

import fr.jamailun.reignofcubes2.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.utils.ItemBuilder;
import fr.jamailun.reignofcubes2.utils.MenuGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;

public class AdminKitsGUI extends MenuGUI {

    public AdminKitsGUI(RocPlayer player) {
        super(6, player, "gui.admin.kits.title");

        // Tri par coût
        List<Kit> kits = kits().getKits()
                .stream()
                .sorted(Comparator.comparing(Kit::getCost))
                .toList();

        // Remplissage
        if(kits.isEmpty()) {
            set(22, new ItemBuilder(Material.RED_CONCRETE).setName("&cVide").toItemStack());
        } else {
            displayKits(kits);
        }

        // Footer and open
        setDefaultFooter();
        open();
    }

    private void displayKits(List<Kit> kits) {
        int slot = 0;
        for(Kit kit : kits) {
            ItemStack is = new ItemBuilder(kit.toIcon())
                    .addLoreLine("Cost: " + (kit.getCost()<0?"§4invalid":"§a"+kit.getCost()))
                    .toItemStack();
            set(slot, is, () -> clickOnKit(kit));
            slot++;
        }
    }

    private void clickOnKit(Kit kit) {
        new AdminKitGUI(getPlayer(), kit);
    }
}
