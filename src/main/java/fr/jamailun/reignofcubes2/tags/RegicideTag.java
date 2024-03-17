package fr.jamailun.reignofcubes2.tags;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.configuration.TagsConfiguration;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A tag for a king-slayer.
 */
public class RegicideTag extends Tag {

    private final GameManager game;

    public RegicideTag(GameManager game) {
        super("regicide");
        this.game = game;
    }

    @Override
    public void added(@NotNull RocPlayer holder) { }

    @Override
    public void removed(@NotNull RocPlayer holder) { }

    @Override
    public void holderAttacks(@NotNull RocPlayer holder, @NotNull RocPlayer other, @NotNull EntityDamageByEntityEvent event) {
        TagsConfiguration config = game.getTagsConfiguration();
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
        TagsConfiguration config = game.getTagsConfiguration();
        boolean targetKing = other.isKing();

        double flatMod = targetKing ? config.getRegicideDefendFlatKing() : config.getRegicideDefendFlatOthers();
        double multMod = targetKing ? config.getRegicideDefendMultiplicativeKing() : config.getRegicideDefendMultiplicativeOthers();

        // Add bonus defense
        double damages = event.getDamage();
        double modDamages = (damages - flatMod) * multMod;
        event.setDamage(modDamages);
    }
}
