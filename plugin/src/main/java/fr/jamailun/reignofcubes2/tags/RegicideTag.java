package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.events.player.RocPlayerAttacksPlayerEvent;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.configuration.TagName;
import fr.jamailun.reignofcubes2.configuration.TagProperty;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A tag for a king-slayer.
 */
@TagName("regicide")
public class RegicideTag extends AbstractRocTag {

    @TagProperty(name = "attack.king.flat", defaultValue = 0)
    double attackKingFlat;

    @TagProperty(name = "attack.king.ratio", defaultValue = 1.2)
    double attackKingRatio;

    @TagProperty(name = "attack.other.flat", defaultValue = 0)
    double attackOtherFlat;

    @TagProperty(name = "attack.other.ratio", defaultValue = 0.8)
    double attackOtherRatio;

    @TagProperty(name = "defend.king.flat", defaultValue = 0)
    double defendKingFlat;

    @TagProperty(name = "defend.king.ratio", defaultValue = 0.85)
    double defendKingRatio;

    @TagProperty(name = "defend.other.flat", defaultValue = 0)
    double defendOtherFlat;

    @TagProperty(name = "defend.other.ratio", defaultValue = 1.15)
    double defendOtherRatio;

    public RegicideTag() {
        super("regicide");
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
