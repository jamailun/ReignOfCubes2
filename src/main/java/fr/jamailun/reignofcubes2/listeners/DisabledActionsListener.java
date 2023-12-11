package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class DisabledActionsListener extends RocListener {
    public DisabledActionsListener(ReignOfCubes2 plugin) {
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
    public void playerInteract(PlayerInteractEvent e) {
        if(e.getAction() == Action.PHYSICAL) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerCraft(CraftItemEvent e) {
        e.setCancelled(true);
    }

}
