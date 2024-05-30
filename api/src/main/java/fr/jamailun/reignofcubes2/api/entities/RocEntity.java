package fr.jamailun.reignofcubes2.api.entities;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface RocEntity {

    @NotNull UUID getUUID();

    @NotNull String getName();

    boolean isValid();

    void teleport(Location location);

}
