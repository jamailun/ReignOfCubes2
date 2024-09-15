package fr.jamailun.reignofcubes2.api.gameplay;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * A mine is a producer of gold.
 */
public interface Mine extends CapturableRegion {

    double getRadius();

    float getMaxStorage();

    float getProductionRate();

    float getStoredGold();

}
