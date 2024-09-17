package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.utils.MenuGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GuiListener extends RocListener {
    public GuiListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler
    void onInventoryClose(@NotNull InventoryCloseEvent e){
        MenuGUI.MenuGUIManager.handleClose(e);
    }

    @EventHandler
    void onInventoryClick(@NotNull InventoryClickEvent e) {
        // Cancel shift item on last slot (when playing)
        if(game().isStatePlaying() && Objects.equals(e.getClickedInventory(), e.getWhoClicked().getInventory())) {
            if(e.getSlot() == 8 || e.getHotbarButton() == 8) {
                e.setCancelled(true);
                return;
            }
        }

        // Transfer event
        MenuGUI.MenuGUIManager.handleClick(e);
    }

}
