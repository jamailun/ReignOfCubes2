package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.configuration.PersistedProperty;
import fr.jamailun.reignofcubes2.api.events.player.RocPlayerAttacksPlayerEvent;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.players.ScoreAddReason;
import fr.jamailun.reignofcubes2.api.players.ScoreRemoveReason;
import fr.jamailun.reignofcubes2.api.tags.TagName;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A tag for a points-stealer.
 */
@TagName("stealer")
public class StealerTag extends AbstractRocTag {

    @PersistedProperty(section = "points", name = "steal-flat")
    int stolenFlat = 0;
    @PersistedProperty(section = "points", name = "steal-ratio")
    double stolenRatio = 0;

    @Override
    public void playerAdded(@NotNull RocPlayer holder) {
        ReignOfCubes2.logDebug(holder.getName() + " is now stealer");
    }

    @Override
    public void playerRemoved(@NotNull RocPlayer holder) {
        ReignOfCubes2.logDebug(holder.getName() + " is NOT stealer anymore.");
    }

    @EventHandler
    void attackEvent(@NotNull RocPlayerAttacksPlayerEvent event) {
        if(!event.getAttacker().isTag(this))
            return;

        double victimPoints = event.getVictim().getScore();
        int stolen = (int) Math.min(victimPoints, victimPoints * stolenRatio + stolenFlat);

        if(stolen > 0) {
            event.getVictim().removeScore(stolen, ScoreRemoveReason.TAG_STEALER);
            event.getAttacker().addScore(stolen, ScoreAddReason.TAG_STEALER);
        }
    }

    @Override
    public boolean isPlayable() {
        return stolenFlat > 0 || stolenRatio > 0;
    }
}
