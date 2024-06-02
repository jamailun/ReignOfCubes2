package fr.jamailun.reignofcubes2.listeners;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.gui.ShopGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractionListener extends RocListener {
    public PlayerInteractionListener(MainROC2 plugin) {
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
                game().isStatePlaying() &&
                (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                p.getInventory().getHeldItemSlot() == 8
        )) {
            return;
        }

        // Open shop
        RocPlayer player = ReignOfCubes2.findPlayer(p);
        if(player != null) {
            new ShopGUI(player);
        }
    }
}
