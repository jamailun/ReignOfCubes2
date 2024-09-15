package fr.jamailun.reignofcubes2.gameplay.throne;

import fr.jamailun.reignofcubes2.GameManagerImpl;
import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.gameplay.CaptureProcess;
import fr.jamailun.reignofcubes2.api.gameplay.Throne;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.utils.MinMax;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents the zone of the throne.
 */
public class ThroneImpl implements Throne {

    private final GameManagerImpl game;
    private final Vector vectorA, vectorB;
    @Getter private final Location center;
    private final Set<RocPlayer> playersInside = new HashSet<>();

    // current
    private RocPlayer king;
    @Nullable private ThroneCapture capture;

    public ThroneImpl(@NotNull World world, @NotNull Vector vectorA, @NotNull Vector vectorB) {
        this.game = GameManagerImpl.instance();
        this.vectorA = Vector.getMinimum(vectorA, vectorB);
        this.vectorB = Vector.getMaximum(vectorA, vectorB);
        this.center = this.vectorB.clone().subtract(this.vectorA).toLocation(world);
    }

    @Override
    public @NotNull Set<RocPlayer> playersInside() {
        return Collections.unmodifiableSet(playersInside);
    }

    @Override
    public boolean isAlreadyInside(@NotNull RocPlayer player) {
        return playersInside.contains(player);
    }

    @Override
    public boolean isInside(@NotNull Location loc) {
        MinMax x = new MinMax(vectorA.getX(), vectorB.getX());
        MinMax y = new MinMax(vectorA.getY(), vectorB.getY());
        MinMax z = new MinMax(vectorA.getZ(), vectorB.getZ());

        return x.contains(loc.getX())
                && y.contains(loc.getY())
                && z.contains(loc.getZ());
    }

    @Override
    public void enters(@NotNull RocPlayer player) {
        playersInside.add(player);

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

        if(playersInside.size() == 1 && canStart && !hasCaptureOngoing()) {
            startCapture(player);
        }
    }

    @Override
    public void leaves(@NotNull RocPlayer player) {
        playersInside.remove(player);

        if(!player.isKing())
            player.sendMessage("throne.leaves");

        if(capture != null && capture.isPlayer(player)) {
            stopCapture();
        }
    }

    private void startCapture(@NotNull RocPlayer player) {
        assert capture == null : "A ceremony already exists.";
        capture = new ThroneCapture(player);
    }

    private void stopCapture() {
        assert capture != null : "No ceremony to stop.";
        capture.stop();
        capture = null;
    }

    @Override
    public void resetCapture() {
        playersInside.clear();
        if(capture != null) {
            stopCapture();
        }
    }

    @Override
    public @Nullable RocPlayer getOwner() {
        return king;
    }

    @Override
    public @Nullable CaptureProcess getCaptureProcess() {
        return capture;
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
        }, game.getActiveConfiguration().getRules().getThroneCooldown());
        return true;
    }
}
