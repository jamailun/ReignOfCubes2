package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.gui.ShopGUI;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractionListener extends RocListener {
    public PlayerInteractionListener(ReignOfCubes2 plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent e) {
        // Disabled ALL physical interaction (i.e. breaking wheat blocks)
        if(e.getAction() == Action.PHYSICAL) {
            e.setCancelled(true);
        }

        // Ignore non-playing state or non right-click.
        Player p = e.getPlayer();
        if(! (
                game().isPlaying() &&
                (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                p.getInventory().getHeldItemSlot() == 8
        )) {
            return;
        }

        // Open shop
        RocPlayer player = game().toPlayer(p);
        if(player != null) {
            new ShopGUI(player);
        }
    }
}
