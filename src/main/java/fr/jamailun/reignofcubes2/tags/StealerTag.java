package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.events.RocPlayerAttacksPlayerEvent;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.players.ScoreAddReason;
import fr.jamailun.reignofcubes2.api.players.ScoreRemoveReason;
import fr.jamailun.reignofcubes2.configuration.sections.TagsConfigurationSection;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A tag for a points-stealer.
 */
public class StealerTag extends AbstractRocTag {

    public StealerTag() {
        super("stealer");
    }

    @Override
    public void playerAdded(@NotNull RocPlayer holder) {
        MainROC2.info("[debug] " + holder.getName() + " is now stealer");
    }

    @Override
    public void playerRemoved(@NotNull RocPlayer holder) {
        MainROC2.info("[debug] " + holder.getName() + " is NOT stealer anymore.");
    }

    @EventHandler
    public void attackEvent(RocPlayerAttacksPlayerEvent event) {
        if(!event.getAttacker().isTag(this))
            return;

        TagsConfigurationSection config = MainROC2.getTags();
        int stole = config.getStealerPointsPerHit();

        if(event.getVictim().hasScore(stole)) {
            event.getVictim().removeScore(stole, ScoreRemoveReason.TAG_STEALER);
            event.getAttacker().addScore(stole, ScoreAddReason.TAG_STEALER);
        }

    }

}
