package fr.jamailun.reignofcubes2.objects;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.utils.MinMax;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents the zone of the throne.
 */
public class Throne {

    private final GameManager game;
    private final Vector vectorA, vectorB;
    private final Set<UUID> playersInside = new HashSet<>();

    // current
    @Getter private Ceremony ceremony;

    public Throne(GameManager game, Vector vectorA, Vector vectorB) {
        this.game = game;
        this.vectorA = vectorA;
        this.vectorB = vectorB;
    }

    public boolean isAlreadyInside(RocPlayer player) {
        return playersInside.contains(player.getUUID());
    }

    /**
     * Custom AABB test. Check if the location is inside the throne zone.
     * @param loc location to test
     * @return true if inside.
     */
    public boolean isInside(Location loc) {
        MinMax x = new MinMax(vectorA.getX(), vectorB.getX());
        MinMax y = new MinMax(vectorA.getY(), vectorB.getY());
        MinMax z = new MinMax(vectorA.getZ(), vectorB.getZ());

        return x.contains(loc.getX())
                && y.contains(loc.getY())
                && z.contains(loc.getZ());
    }

    public void enters(RocPlayer player) {
        playersInside.add(player.getUUID());

        boolean canStart = true;
        if(game.hasKing()) {
            if(player.isKing()) {
                player.sendMessage("throne.enters-as-king");
                canStart = false;
            } else {
                player.sendMessage("throne.enters-but-king");
            }
        } else {
            player.sendMessage("throne.enters");
        }

        if(playersInside.size() == 1 && canStart && !hasCeremony()) {
            startCeremony(player);
        }
    }

    public void leaves(RocPlayer player) {
        playersInside.remove(player.getUUID());

        if(!player.isKing())
            player.sendMessage("throne.leaves");

        if(hasCeremony() && ceremony.isPlayer(player)) {
            stopCeremony();
        }
    }

    public boolean hasCeremony() {
        return ceremony != null;
    }

    private void startCeremony(RocPlayer player) {
        assert ceremony == null : "A ceremony already exists.";
        ceremony = new Ceremony(game, player);
    }

    private void stopCeremony() {
        assert ceremony != null : "No ceremony to stop.";
        ceremony.stop();
        ceremony = null;
    }

    public void reset() {
        if(ceremony != null)
            stopCeremony();
    }

}
