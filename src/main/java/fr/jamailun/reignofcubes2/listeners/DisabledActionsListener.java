package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Listener to prevent some actions.
 */
public class DisabledActionsListener extends RocListener {
  public DisabledActionsListener(@NotNull ReignOfCubes2 plugin) {
    super(plugin);
  }

  @EventHandler
  void playerDrop(@NotNull PlayerDropItemEvent e) {
    if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  void playerBreakBlock(@NotNull BlockBreakEvent e) {
    if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  void playerPlaceBlock(@NotNull BlockPlaceEvent e) {
    if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  void playerCraft(@NotNull CraftItemEvent e) {
    if (e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  void playerPickupArrow(@NotNull PlayerPickupArrowEvent e) {
    e.setCancelled(true);
    e.getArrow().remove();
  }
}
