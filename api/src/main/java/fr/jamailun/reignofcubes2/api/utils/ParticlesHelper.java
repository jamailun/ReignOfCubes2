package fr.jamailun.reignofcubes2.api.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Set;

public final class ParticlesHelper {
    private ParticlesHelper() {}

    public static void playCircleXZ(Collection<Player> targets, Location center, double radius, double delta, Particle particle) {
        assert delta > 0;
        assert radius > 0;
        for(double theta = 0; theta < Math.PI * 2; theta += delta) {
            double cos = radius * Math.cos(theta);
            double sin = radius * Math.sin(theta);
            targets.forEach(p -> p.spawnParticle(
                    particle,
                    center.getX() + cos, center.getY(), center.getZ() + sin,
                    1, // count
                    0, 0, 0, // offset
                    0 // speed
            ));
        }
    }

    public static void playCircleXZ(Player target, Location center, double radius, double delta, Particle particle) {
        playCircleXZ(Set.of(target), center, radius, delta, particle);
    }

    public static void playLine(Player target, Location a, Location b, double delta, Particle particle) {
        Vector dir = b.toVector().subtract(a.toVector()).normalize();
        double dist = a.distance(b);
        for(double d = 0; d <= dist; d += delta) {
            target.spawnParticle(
                    particle,
                    a.getX() + (dir.getX() * d), a.getY() + (dir.getY() * d), a.getZ() + (dir.getZ() * d),
                    1, // count
                    0, 0, 0, // offset
                    0 // speed
            );
        }
    }

    public static void playLine(Location a, Location b, double delta, Particle particle) {
        Bukkit.getOnlinePlayers().forEach(p -> playLine(p, a, b, delta, particle));
    }

    public static void playBox(Player target, Location first, Location second, double delta, Particle particle) {
        Location min = new Location(
                first.getWorld(),
                Math.min(first.getX(), second.getX()),
                Math.min(first.getY(), second.getY()),
                Math.min(first.getZ(), second.getZ())
        );
        Location max = new Location(
                first.getWorld(),
                Math.max(first.getX(), second.getX()),
                Math.max(first.getY(), second.getY()),
                Math.max(first.getZ(), second.getZ())
        );

        Vector dx = new Vector(max.getX() - min.getX(), 0, 0);
        Vector dy = new Vector(0, max.getY() - min.getY(), 0);
        Vector dz = new Vector(0, 0, max.getZ() - min.getZ());

        Location b = min.clone().add(dx);
        Location c = b.clone().add(dz);
        Location d = min.clone().add(dz);
        Location e = min.clone().add(dy);
        Location f = b.clone().add(dy);
        Location h = d.clone().add(dy);
        // lower plane
        playLine(target, min, b, delta, particle);
        playLine(target, min, d, delta, particle);
        playLine(target, b, c, delta, particle);
        playLine(target, d, c, delta, particle);
        // upper place
        playLine(target, e, f, delta, particle);
        playLine(target, e, h, delta, particle);
        playLine(target, f, max, delta, particle);
        playLine(target, h, max, delta, particle);
        // pillars
        playLine(target, min, e, delta, particle);
        playLine(target, b, f, delta, particle);
        playLine(target, c, max, delta, particle);
        playLine(target, d, h, delta, particle);
    }

}
