package fr.jamailun.reignofcubes2;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Throne {

    private final Location positionA, positionB;
    private final Set<UUID> playersInside = new HashSet<>();

    public Throne(World world, Vector vectorA, Vector vectorB) {
        positionA = vectorA.toLocation(world);
        positionB = vectorB.toLocation(world);
    }



}
