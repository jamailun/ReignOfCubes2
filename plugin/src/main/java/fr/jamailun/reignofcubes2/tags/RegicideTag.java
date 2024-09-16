package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.configuration.PersistedProperty;
import fr.jamailun.reignofcubes2.api.events.player.RocPlayerAttacksPlayerEvent;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.tags.TagName;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A tag for a king-slayer.
 */
@TagName("regicide")
public class RegicideTag extends AbstractRocTag {

    @PersistedProperty(section = "attack", name = "king.flat")
    double attackKingFlat = 0;
    @PersistedProperty(section = "attack", name = "king.ratio")
    double attackKingRatio = 1.2;

    @PersistedProperty(section = "attack", name = "other.flat")
    double attackOtherFlat = 0;
    @PersistedProperty(section = "attack", name = "other.ratio")
    double attackOtherRatio = 0.8;

    @PersistedProperty(section = "defend", name = "king.flat")
    double defendKingFlat = 0;
    @PersistedProperty(section = "defend", name = "king.ratio")
    double defendKingRatio = 0.85;

    @PersistedProperty(section = "defend", name = "other.flat")
    double defendOtherFlat = 0;
    @PersistedProperty(section = "defend", name = "other.ratio")
    double defendOtherRatio = 1.15;

    @Override
    public boolean isPlayable() {
        return false;
    }

    @Override
    public void playerAdded(@NotNull RocPlayer holder) {
        ReignOfCubes2.logDebug(holder.getName() + " is now REGICIDE");
    }

    @Override
    public void playerRemoved(@NotNull RocPlayer holder) {
        ReignOfCubes2.logDebug(holder.getName() + " is not regicide anymore.");
    }

    @EventHandler
    void attackEvent(@NotNull RocPlayerAttacksPlayerEvent event) {
        // If the attacker is regicide
        if(event.getAttacker().isTag(this)) {
            boolean targetKing = event.getVictim().isKing();
            double flatMod = targetKing ? attackKingFlat : attackOtherFlat;
            double multMod = targetKing ? attackKingRatio : attackOtherRatio;
            // Add bonus damages
            double damages = event.getBukkitEvent().getDamage();
            double modDamages = (damages + flatMod) * multMod;
            event.getBukkitEvent().setDamage(modDamages);
        }

        // If the defender is regicide
        if(event.getVictim().isTag(this)) {
            boolean attackerKing = event.getAttacker().isKing();
            double flatMod = attackerKing ? defendKingFlat : defendOtherFlat;
            double multMod = attackerKing ? defendKingRatio : defendOtherRatio;
            // Add bonus defense
            double damages = event.getBukkitEvent().getDamage();
            double modDamages = (damages - flatMod) * multMod;
            event.getBukkitEvent().setDamage(modDamages);
        }
    }

}
