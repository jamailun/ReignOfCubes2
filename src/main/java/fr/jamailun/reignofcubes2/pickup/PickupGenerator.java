package fr.jamailun.reignofcubes2.pickup;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.messages.Messages;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class PickupGenerator {

    @Getter private final Location location;
    @Setter private double seconds;

    private BukkitTask task;

    private Item onGroundItem;

    public PickupGenerator(Location location, double seconds) {
        this.location = location;
        this.seconds = seconds;
    }

    private void startWaiting() {
        cancel(); // Ne mange pas de pain
        task = ReignOfCubes2.runTaskLater(this::spawnItem, seconds);
    }

    private void spawnItem() {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.displayName(Messages.parseComponent("<green>points"));
        item.setItemMeta(meta);

        onGroundItem = location.getWorld().dropItem(location, item);
    }

    public void start() {
        startWaiting();
    }

    public void cancel() {
        if(task != null) {
            task.cancel();
            task = null;
        }

        if(onGroundItem != null) {
            onGroundItem.remove();
            onGroundItem = null;
        }
    }

    public boolean itemPickedUp(@NotNull Item item) {
        if(onGroundItem == null)
            return false;
        if(item.getUniqueId().equals(onGroundItem.getUniqueId())) {
            onGroundItem.remove();
            startWaiting();
            return true;
        }
        return false;
    }

}
