package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.TagsConfiguration;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A tag for a king-slayer.
 */
public class RegicideTag extends RocTag {

    public RegicideTag() {
        super("regicide");
    }

    @Override
    public void added(@NotNull RocPlayer holder) {
        ReignOfCubes2.info("[debug] " + holder.getName() + " is now REGICIDE");
    }

    @Override
    public void removed(@NotNull RocPlayer holder) {
        ReignOfCubes2.info("[debug] " + holder.getName() + " is not regicide anymore.");
    }

    @Override
    public void holderAttacks(@NotNull RocPlayer holder, @NotNull RocPlayer other, @NotNull EntityDamageByEntityEvent event) {
        TagsConfiguration config = ReignOfCubes2.getTags();
        boolean targetKing = other.isKing();

        double flatMod = targetKing ? config.getRegicideAttackFlatKing() : config.getRegicideAttackFlatOthers();
        double multMod = targetKing ? config.getRegicideAttackMultiplicativeKing() : config.getRegicideAttackMultiplicativeOthers();

        // Add bonus damages
        double damages = event.getDamage();
        double modDamages = (damages + flatMod) * multMod;
        event.setDamage(modDamages);
    }

    @Override
    public void holderDefends(@NotNull RocPlayer holder, @Nullable RocPlayer other, @NotNull EntityDamageEvent event) {
        if(other == null) // only care when attacker is non-null
            return;
        TagsConfiguration config = ReignOfCubes2.getTags();
        boolean attackerKing = other.isKing();

        double flatMod = attackerKing ? config.getRegicideDefendFlatKing() : config.getRegicideDefendFlatOthers();
        double multMod = attackerKing ? config.getRegicideDefendMultiplicativeKing() : config.getRegicideDefendMultiplicativeOthers();

        // Add bonus defense
        double damages = event.getDamage();
        double modDamages = (damages - flatMod) * multMod;
        event.setDamage(modDamages);
    }
}
