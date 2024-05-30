package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.TagsConfiguration;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.players.ScoreAddReason;
import fr.jamailun.reignofcubes2.players.ScoreRemoveReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A tag for a points-stealer.
 */
public class StealerTag extends RocTag {

    public StealerTag() {
        super("stealer");
    }

    @Override
    public void added(@NotNull RocPlayer holder) {
        ReignOfCubes2.info("[debug] " + holder.getName() + " is now stealer");
    }

    @Override
    public void removed(@NotNull RocPlayer holder) {
        ReignOfCubes2.info("[debug] " + holder.getName() + " is NOT stealer anymore.");
    }

    @Override
    public void holderAttacks(@NotNull RocPlayer holder, @NotNull RocPlayer other, @NotNull EntityDamageByEntityEvent event) {
        TagsConfiguration config = ReignOfCubes2.getTags();
        int stole = config.getStealerPointsPerHit();

        if(other.hasScore(stole)) {
            other.removeScore(stole, ScoreRemoveReason.TAG_STEALER);
            holder.addScore(stole, ScoreAddReason.TAG_STEALER);
        }
    }

    @Override
    public void holderDefends(@NotNull RocPlayer holder, @Nullable RocPlayer other, @NotNull EntityDamageEvent event) {
        // nothing
    }
}
