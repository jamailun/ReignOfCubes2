package fr.jamailun.reignofcubes2.utils;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.messages.Messages;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class MenuGUI {

    protected final static ItemStack EMPTY_SLOT = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();

    @Getter private final int size;
    @Getter private final Component title;
    @Getter private final RocPlayer viewer;

    @Getter private final Inventory inventory;
    @Getter private final Map<Integer, Runnable> options = new HashMap<>();

    public MenuGUI(int lines, String titleEntry, RocPlayer viewer) {
        assert lines > 0 && (lines <= 9 || lines % 9 == 0) : "Invalid MenuGUI size ("+lines+")";
        this.size = (lines < 9) ? lines * 9 : lines;
        this.viewer = viewer;
        title = Messages.formatComponent(viewer.getLanguage(), titleEntry);

        // Create inventory
        inventory = Bukkit.createInventory(viewer.getPlayer(), size, title);
        fill(EMPTY_SLOT);
    }

    protected void open() {
        viewer.getPlayer().openInventory(inventory);
        onOpen();
    }

    protected void close() {
        viewer.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        afterClose(true);
    }

    protected RocPlayer getPlayer() {
        return viewer;
    }

    protected MenuGUI set(int slot, ItemBuilder item) {
        return set(slot, item.toItemStack());
    }

    protected MenuGUI set(int slot, ItemBuilder item, Runnable action) {
        options.put(slot, action);
        return set(slot, item);
    }

    protected MenuGUI set(int slot, ItemStack item) {
        inventory.setItem(slot, item);
        return this;
    }

    protected MenuGUI set(int slot, ItemStack item, Runnable action) {
        options.put(slot, action);
        return set(slot, item);
    }

    protected void fill(int min, int max, ItemStack item) {
        for(int i = min; i < max; i++) {
            set(i, item);
        }
    }

    protected void fill(ItemStack item) {
        fill(0, size, item);
    }

    public void onOpen() {}

    public final void handleClick(InventoryClickEvent e) {
        if(options.containsKey(e.getSlot())) {
            options.get(e.getSlot()).run();
            return;
        }
        onClick(e);
    }

    public abstract void onClick(InventoryClickEvent e);

    public void afterClose(boolean internal) {}


    public final static class MenuGUIManager {
        private MenuGUIManager() {}
        private static final Set<MenuGUI> GUIS = new HashSet<>();

        public static void handleClick(InventoryClickEvent event) {
            if(event == null || event.getClickedInventory() == null) return;
            for(MenuGUI gui : GUIS) {
                if(gui.inventory.getType() == event.getClickedInventory().getType()
                        && gui.title.equals(event.getView().title())
                        && gui.inventory.getViewers().equals(event.getClickedInventory().getViewers())
                ){
                    gui.handleClick(event);
                    return;
                }
            }
        }

        public static void handleClose(InventoryCloseEvent event) {
            if(event == null) return;
            for(MenuGUI gui : GUIS) {
                if(gui.inventory.getType() == event.getInventory().getType()
                        && gui.title.equals(event.getView().title())
                        && gui.inventory.getViewers().equals(event.getInventory().getViewers())
                ){
                    gui.afterClose(false);
                    if(!GUIS.remove(gui)) {
                        ReignOfCubes2.error("This way be a problem. Could not remove gui after having found it.");
                    }
                    return;
                }
            }
        }



    }

}
