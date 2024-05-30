package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class DisabledActionsListener extends RocListener {
    public DisabledActionsListener(MainROC2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerDrop(PlayerDropItemEvent e) {
        if(e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerBreakBlock(BlockBreakEvent e) {
        if(e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerPlaceBlock(BlockPlaceEvent e) {
        if(e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerCraft(CraftItemEvent e) {
        if(e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerSwapItems(PlayerSwapHandItemsEvent e) {
        if(e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerPickupArrow(PlayerPickupArrowEvent e) {
        e.setCancelled(true);
        e.getArrow().remove();
    }

}
