package fr.jamailun.reignofcubes2.gameplay;

import fr.jamailun.reignofcubes2.GameManagerImpl;
import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.gameplay.Ceremony;
import fr.jamailun.reignofcubes2.api.gameplay.Throne;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.utils.MinMax;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents the zone of the throne.
 */
public class ThroneImpl implements Throne {

    private final GameManagerImpl game;
    private final Vector vectorA, vectorB;
    private final Set<UUID> playersInside = new HashSet<>();

    // current
    @Getter @Nullable private Ceremony ceremony;

    public ThroneImpl(GameManagerImpl game, Vector vectorA, Vector vectorB) {
        this.game = game;
        this.vectorA = vectorA;
        this.vectorB = vectorB;
    }

    @Override
    public boolean isAlreadyInside(@NotNull RocPlayer player) {
        return playersInside.contains(player.getUUID());
    }

    @Override
    public boolean isInside(Location loc) {
        MinMax x = new MinMax(vectorA.getX(), vectorB.getX());
        MinMax y = new MinMax(vectorA.getY(), vectorB.getY());
        MinMax z = new MinMax(vectorA.getZ(), vectorB.getZ());

        return x.contains(loc.getX())
                && y.contains(loc.getY())
                && z.contains(loc.getZ());
    }

    @Override
    public void enters(RocPlayer player) {
        playersInside.add(player.getUUID());

        boolean canStart = true;
        if(game.hasKing()) {
            if(player.isKing()) {
                player.sendMessage("throne.enters-as-king");
                canStart = false;
                //TODO give effects
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

    @Override
    public void leaves(RocPlayer player) {
        playersInside.remove(player.getUUID());

        if(!player.isKing())
            player.sendMessage("throne.leaves");

        if(hasCeremony() && ceremony.isPlayer(player)) {
            stopCeremony();
        }
    }

    @Override
    public boolean hasCeremony() {
        return ceremony != null;
    }

    private void startCeremony(RocPlayer player) {
        assert ceremony == null : "A ceremony already exists.";
        ceremony = new CeremonyImpl(game, player);
    }

    private void stopCeremony() {
        assert ceremony != null : "No ceremony to stop.";
        ceremony.stop();
        ceremony = null;
    }

    @Override
    public void resetCeremony() {
        playersInside.clear();
        if(ceremony != null) {
            stopCeremony();
        }
    }

    // COOLDOWNS

    private final Set<UUID> playersCooldowns = new HashSet<>();
    private final Object pcKey = new Object();

    @Override
    public boolean isCooldownOk(UUID uuid) {
        if(playersCooldowns.contains(uuid)) {
            return false;
        }
        // Add it now
        synchronized (pcKey) {
            playersCooldowns.add(uuid);
        }
        // Remove it later
        MainROC2.runTaskLater(() -> {
            synchronized (pcKey) {
                playersCooldowns.remove(uuid);
            }
        }, game.getConfiguration().getRules().getThroneCooldown());
        return true;
    }

}
