package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Throne {

    private final GameManager game;
    private final double crowningDuration;
    private final Vector vectorA, vectorB;
    private final Set<UUID> playersInside = new HashSet<>();

    // current
    private Ceremony ceremony;

    public Throne(GameManager game, Vector vectorA, Vector vectorB, double crowningDuration) {
        this.game = game;
        this.vectorA = vectorA;
        this.vectorB = vectorB;
        this.crowningDuration = crowningDuration;
    }

    public boolean isAlreadyInside(RocPlayer player) {
        return playersInside.contains(player.getUUID());
    }

    public boolean isInside(Location loc) {
        return loc.toVector().isInAABB(vectorA, vectorB);
    }

    public void enters(RocPlayer player) {
        playersInside.add(player.getUUID());
        player.sendMessage("throne.enters");

        if(playersInside.size() == 1) {
            if( ! game.hasKing()) {
                startCeremony(player);
            }
        }
    }

    public void leaves(RocPlayer player) {
        playersInside.remove(player.getUUID());
        if(!player.isKing())
            player.sendMessage("throne.leaves");

        if(ceremony != null && ceremony.isPlayer(player)) {
            stopCeremony();
        }
    }

    private void startCeremony(RocPlayer player) {
        assert ceremony == null : "A ceremony already exists.";
        ceremony = new Ceremony(game, player, crowningDuration);
    }

    private void stopCeremony() {
        assert ceremony != null : "No ceremony to stop.";
        ceremony.stop();
        ceremony = null;
    }

}
