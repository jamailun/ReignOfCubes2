package fr.jamailun.reignofcubes2.pickup;

import fr.jamailun.reignofcubes2.configuration.pickups.PickupConfigEntry;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Manages all {@link PickupGenerator generators}.
 */
public class PickupsManager {

    private final List<PickupGenerator> generators = new ArrayList<>();
    private final Supplier<PickupConfigEntry> supplier;

    /**
     * Create a new PickupManager
     * @param supplier the producer of random entries.
     */
    public PickupsManager(Supplier<PickupConfigEntry> supplier) {
        this.supplier = supplier;
    }

    /**
     * Try to make an Item correspond with generators item.
     * @param item the item-entity to test.
     * @return a non-empty optional if Generator produced the item.
     */
    public Optional<PickupConfigEntry> tryPickupItem(@NotNull Item item) {
        for(PickupGenerator generator : generators) {
            Optional<PickupConfigEntry> opt = generator.itemPickedUp(item);
            if(opt.isPresent())
                return opt;
        }
        return Optional.empty();
    }

    public void regenerateAll(List<Location> locations, double duration) {
        purgeAndClear();
        //
        locations.forEach(l -> addGenerator(l, duration));
    }

    public void addGenerator(Location location, double duration) {
        generators.add(new PickupGenerator(location, duration, supplier));
    }

    public void start(List<Location> locations, double duration) {
        regenerateAll(locations, duration);
        generators.forEach(PickupGenerator::start);
    }

    public void purgeAndStop() {
        generators.forEach(PickupGenerator::cancel);
    }

    public void purgeAndClear() {
        purgeAndStop();
        generators.clear();
    }

}
