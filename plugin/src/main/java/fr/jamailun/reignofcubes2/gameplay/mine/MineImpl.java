package fr.jamailun.reignofcubes2.gameplay.mine;

import fr.jamailun.reignofcubes2.GameManagerImpl;
import fr.jamailun.reignofcubes2.api.gameplay.Mine;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.utils.ParticlesHelper;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Standard implementation for a Mine.
 */
public class MineImpl implements Mine {

    private final GameManagerImpl game;
    @Getter @NotNull private final Location center;
    @Getter private final double radius, radiusSquared;
    @Getter private final float productionRate;
    @Getter private final float maxStorage;

    @Getter private float storedGold = 0;
    private final Set<RocPlayer> playersInside = new HashSet<>();

    // Transient
    @Getter @Nullable private RocPlayer owner;
    private MineCapture capture;

    public MineImpl(@NotNull Location location, double radius, float productionRate, float maxStorage) {
        game = GameManagerImpl.instance();
        this.center = location;
        this.radius = radius;
        this.radiusSquared = radius * radius;
        this.productionRate = productionRate;
        this.maxStorage = maxStorage;
    }

    private int playersCount() {
        return playersInside.size() + (owner == null ? 0 : 1);
    }

    public void tick(float multiplier) {
        // Pour tous les joueurs, on rajoute (ou retire) ceux qui sont ou non plus dans la zone.
        for(RocPlayer player : game.getPlayers()) {
            if(isInside(player.getPlayer().getLocation())) {
                enters(player);
            } else {
                leaves(player);
            }
        }

        float produced = productionRate * multiplier;

        // Redistribute to players inside
        if( ! playersInside.isEmpty()) {
            float toConsume = Math.max(produced, storedGold);
            float perPlayer = toConsume / playersCount();
            playersInside.forEach(r -> addGold(r, perPlayer));
            ParticlesHelper.playCircleXZ(
                    playersInside.stream().map(RocPlayer::getPlayer).toList(),
                    center,
                    radius,
                    0.5,
                    Particle.DRIPPING_HONEY
            );
        }

        // Store more gold
        storedGold += produced;
        if(storedGold > maxStorage)
            storedGold = maxStorage;
    }

    private void addGold(@NotNull RocPlayer player, float perPlayer) {
        boolean isOwner = isOwner(player);
        player.addGold(perPlayer * (isOwner ? 2f : 1f));
    }

    public void reset() {
        storedGold = 0;
        playersInside.clear();
    }

    @Override
    public @Nullable MineCapture getCaptureProcess() {
        return capture;
    }

    @Override
    public @NotNull Set<RocPlayer> playersInside() {
        return Collections.unmodifiableSet(playersInside);
    }

    @Override
    public void enters(@NotNull RocPlayer player) {
        playersInside.add(player);
    }

    @Override
    public void leaves(@NotNull RocPlayer player) {
        playersInside.remove(player);
    }

    @Override
    public boolean isInside(@NotNull Location location) {
        return getCenter().distanceSquared(location) <= radiusSquared;
    }
}
