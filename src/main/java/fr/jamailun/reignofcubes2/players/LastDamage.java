package fr.jamailun.reignofcubes2.players;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

@Getter
public class LastDamage {

    private final RocPlayer damager;
    private final Instant instant;

    public LastDamage(@Nullable RocPlayer damager) {
        this.damager = damager;
        this.instant = Instant.now();
    }

    public double millisSince() {
        return (double) Duration.between(instant, Instant.now()).toMillis();
    }

}
