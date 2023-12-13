package fr.jamailun.reignofcubes2.gui;

import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.utils.MenuGUI;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AdminKitsGUI extends MenuGUI {

    public AdminKitsGUI(RocPlayer player) {
        super(3, "gui.admin.kits.title", player);



        open();
    }

    @Override
    public void onClick(InventoryClickEvent e) {

    }
}
