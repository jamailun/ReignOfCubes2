package fr.jamailun.reignofcubes2.pickup;

import org.bukkit.Location;
import org.bukkit.entity.Item;

import java.util.ArrayList;
import java.util.List;

public class PickupsManager {

    private final List<PickupGenerator> generators = new ArrayList<>();

    public boolean tryPickupItem(Item item) {
        return generators.stream()
                .anyMatch(g -> g.itemPickedUp(item));
    }

    public void regenerateAll(List<Location> locations, double duration) {
        purgeAndClear();
        //
        locations.forEach(l -> addGenerator(l, duration));
    }

    public void addGenerator(Location location, double duration) {
        generators.add(new PickupGenerator(location, duration));
    }

    public void removeClosest(Location location) {
        if(generators.isEmpty())
            return;

        PickupGenerator closest = null;
        double distance = Double.MAX_VALUE;
        for(PickupGenerator generator : generators) {
            double sqDistance = generator.getLocation().distanceSquared(location);
            if(sqDistance < distance) {
                distance = sqDistance;
                closest = generator;
            }
        }
        assert closest != null;
        generators.remove(closest);
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
