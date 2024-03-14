package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.utils.MenuGUI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Objects;

public class GuiListener extends RocListener {
    public GuiListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        MenuGUI.MenuGUIManager.handleClose(e);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        // Cancel shift item on last slot (when playing)
        if(game().isPlaying() && Objects.equals(e.getClickedInventory(), e.getWhoClicked().getInventory())) {
            if(e.getSlot() == 8 || e.getHotbarButton() == 8) {
                e.setCancelled(true);
                return;
            }
        }

        // Transfer event
        MenuGUI.MenuGUIManager.handleClick(e);
    }

}
