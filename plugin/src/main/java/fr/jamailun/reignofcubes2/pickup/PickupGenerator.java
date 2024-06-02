package fr.jamailun.reignofcubes2.pickup;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.configuration.pickups.PickupConfigEntry;
import fr.jamailun.reignofcubes2.messages.Messages;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class PickupGenerator {

    @Getter private final Location location;
    @Setter private double seconds;
    private final Supplier<PickupConfigEntry> supplier;

    private BukkitTask task;

    private PickupConfigEntry lastEntry;
    private Item onGroundItem;

    public PickupGenerator(Location location, double seconds, Supplier<PickupConfigEntry> supplier) {
        this.location = location;
        this.seconds = seconds;
        this.supplier = supplier;
    }

    private void startWaiting() {
        cancel(); // Ne mange pas de pain
        task = MainROC2.runTaskLater(this::spawnItem, seconds);
    }

    private void spawnItem() {
        lastEntry = supplier.get();
        ItemStack item = new ItemStack(lastEntry.material());
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.displayName(Messages.parseComponent("<green>"+lastEntry.score()+" points"));
        item.setItemMeta(meta);

        onGroundItem = location.getWorld().dropItem(location, item);
        onGroundItem.setVelocity(new Vector(0, 0, 0));
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
            lastEntry = null;
        }
    }

    public Optional<PickupConfigEntry> itemPickedUp(@NotNull Item item) {
        if(onGroundItem == null)
            return Optional.empty();
        if(item.getUniqueId().equals(onGroundItem.getUniqueId())) {
            onGroundItem.remove();
            PickupConfigEntry entry = lastEntry;
            startWaiting();
            return Optional.of(entry);
        }
        return Optional.empty();
    }

}
