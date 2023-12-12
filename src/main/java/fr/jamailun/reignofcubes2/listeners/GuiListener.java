package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.utils.MenuGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiListener extends RocListener {
    public GuiListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        MenuGUI.MenuGUIManager.handleClose(e);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        MenuGUI.MenuGUIManager.handleClick(e);
    }

}
