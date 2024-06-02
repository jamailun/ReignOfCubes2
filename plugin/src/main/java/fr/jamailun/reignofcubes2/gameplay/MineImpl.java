package fr.jamailun.reignofcubes2.gameplay;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.gameplay.Mine;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.utils.ParticlesHelper;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class MineImpl implements Mine {

    @Getter @NotNull private final Location location;
    @Getter private final double radius;
    @Getter private final float productionRate;
    @Getter private final float maxStorage;

    @Getter private float storedGold = 0;
    private Set<RocPlayer> playersInside = new HashSet<>();

    public MineImpl(@NotNull Location location, double radius, float productionRate, float maxStorage) {
        this.location = location;
        this.radius = radius;
        this.productionRate = productionRate;
        this.maxStorage = maxStorage;
    }

    public void tick(float multiplier) {
        computePlayersInside();

        // Redistribute to players inside
        if( ! playersInside.isEmpty()) {
            float toConsume = Math.max(10 * multiplier, storedGold);
            float perPlayer = toConsume / playersInside.size();
            playersInside.forEach(r -> r.addGold(perPlayer));
            ParticlesHelper.playCircleXZ(
                    playersInside.stream().map(RocPlayer::getPlayer).toList(),
                    location,
                    radius,
                    0.5,
                    Particle.DRIPPING_HONEY
            );
        }

        // Store more gold
        storedGold += (productionRate * multiplier);
        if(storedGold > maxStorage)
            storedGold = maxStorage;
    }

    private void computePlayersInside() {
        playersInside = location.getNearbyPlayers(radius).stream()
                .map(ReignOfCubes2::findPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public void reset() {
        storedGold = 0;
        playersInside.clear();
    }

    @Override
    public Set<RocPlayer> getPlayersInside() {
        return Collections.unmodifiableSet(playersInside);
    }

}
