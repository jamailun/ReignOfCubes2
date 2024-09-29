package fr.jamailun.reignofcubes2.players;

import fr.jamailun.reignofcubes2.api.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.players.ScoreAddReason;
import fr.jamailun.reignofcubes2.api.players.ScoreRemoveReason;
import fr.jamailun.reignofcubes2.api.tags.RocTag;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Wraps a spectator.
 */
public class RocSpectator extends BasicRocPlayer {

    public RocSpectator(@NotNull Player player) {
        super(player);
    }

    @Override
    public void addScore(int delta, @NotNull ScoreAddReason reason) {}

    @Override
    public boolean hasScore(int score) {
        return false;
    }

    @Override
    public void removeScore(int delta, @NotNull ScoreRemoveReason reason) {}

    @Override
    public boolean isKing() {
        return false;
    }

    @Override
    public void setKing(boolean isKing) {}

    @Override
    public int getLastMoneySpent() {
        return 0;
    }

    @Override
    public void buyKit(@NotNull Kit kit) {}

    @Override
    public float getGold() {
        return 0;
    }

    @Override
    public boolean hasGold(float value) {
        return false;
    }

    @Override
    public void addGold(float delta) {}

    @Override
    public void removeGold(float delta) { }

    @Override
    public int getScore() {
        return 0;
    }

    @Override
    public void setLastDamager(@NotNull RocPlayer damager) {  }

    @Override
    public @Nullable RocPlayer getLastDamager() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(obj instanceof RocSpectator rp) {
            return Objects.equals(rp.getUUID(), getUUID());
        }
        return false;
    }

    @Override
    public void setTag(@Nullable RocTag tag) {}

    @Override
    public void clearTag() {}

    @Override
    public boolean hasTag() {
        return false;
    }

    @Override
    public Optional<RocTag> getTag() {
        return Optional.empty();
    }

    @Override
    public boolean isSpectator() {
        return true;
    }

    @Override
    public void reset() {
        player.setGameMode(GameMode.SPECTATOR);
    }
}
