package fr.jamailun.reignofcubes2.gui;

import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.utils.MenuGUI;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ShopGUI extends MenuGUI {

    public ShopGUI(RocPlayer player) {
        super(3, player, "gui.shop.title");



        open();
    }

    private void clickBuy() {

    }

    @Override
    public void onClick(InventoryClickEvent e) {

    }
}
