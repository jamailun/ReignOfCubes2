package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.events.player.RocPlayerAttacksPlayerEvent;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.configuration.sections.TagsConfigurationSection;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A tag for a king-slayer.
 */
public class RegicideTag extends AbstractRocTag {

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
    public void attackEvent(RocPlayerAttacksPlayerEvent event) {
        TagsConfigurationSection config = tags();

        // If the attacker is regicide
        if(event.getAttacker().isTag(this)) {
            boolean targetKing = event.getVictim().isKing();
            double flatMod = targetKing ? config.getRegicideAttackFlatKing() : config.getRegicideAttackFlatOthers();
            double multMod = targetKing ? config.getRegicideAttackMultiplicativeKing() : config.getRegicideAttackMultiplicativeOthers();
            // Add bonus damages
            double damages = event.getBukkitEvent().getDamage();
            double modDamages = (damages + flatMod) * multMod;
            event.getBukkitEvent().setDamage(modDamages);
        }

        // If the defender is regicide
        if(event.getVictim().isTag(this)) {
            boolean attackerKing = event.getAttacker().isKing();
            double flatMod = attackerKing ? config.getRegicideDefendFlatKing() : config.getRegicideDefendFlatOthers();
            double multMod = attackerKing ? config.getRegicideDefendMultiplicativeKing() : config.getRegicideDefendMultiplicativeOthers();
            // Add bonus defense
            double damages = event.getBukkitEvent().getDamage();
            double modDamages = (damages - flatMod) * multMod;
            event.getBukkitEvent().setDamage(modDamages);
        }
    }

}
