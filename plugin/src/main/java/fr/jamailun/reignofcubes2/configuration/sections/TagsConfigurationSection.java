package fr.jamailun.reignofcubes2.configuration.sections;

import fr.jamailun.reignofcubes2.api.configuration.sections.DeserializeConfiguration;
import fr.jamailun.reignofcubes2.api.configuration.sections.RocConfigurationSection;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Configuration for native tags.
 */
@Getter
@Setter
public class TagsConfigurationSection extends RocConfigurationSection {

    // Regicide
    private double regicideAttackFlatKing;
    private double regicideAttackMultiplicativeKing;
    private double regicideAttackFlatOthers;
    private double regicideAttackMultiplicativeOthers;
    private double regicideDefendFlatKing;
    private double regicideDefendMultiplicativeKing;
    private double regicideDefendFlatOthers;
    private double regicideDefendMultiplicativeOthers;

    // Stealer
    private int stealerPointsPerHit;

    @DeserializeConfiguration
    public static @Nonnull TagsConfigurationSection load(@Nullable ConfigurationSection config) {
        if(config == null)
            return defaultConfiguration();

        TagsConfigurationSection rules = new TagsConfigurationSection();
        // Regicide
        ConfigurationSection regicide = config.getConfigurationSection("regicide");
        if(regicide != null) {
            rules.regicideAttackFlatKing = regicide.getDouble("attack-king-flat", 0);
            rules.regicideAttackMultiplicativeKing = regicide.getDouble("attack-king-mult", 1);
            rules.regicideAttackFlatOthers = regicide.getDouble("attack-others-flat", 0);
            rules.regicideAttackMultiplicativeOthers = regicide.getDouble("attack-others-mult", 1);
            rules.regicideDefendFlatKing = regicide.getDouble("defend-king-flat", 0);
            rules.regicideDefendMultiplicativeKing = regicide.getDouble("defend-king-mult", 1);
            rules.regicideDefendFlatOthers = regicide.getDouble("defend-others-flat", 0);
            rules.regicideDefendMultiplicativeOthers = regicide.getDouble("defend-others-mult", 1);
        }
        // Stealer
        ConfigurationSection stealer = config.getConfigurationSection("stealer");
        if(stealer != null) {
            rules.stealerPointsPerHit = stealer.getInt("points-per-hit", 0);
        }

        return rules;
    }

    @Override
    public void write(@NotNull ConfigurationSection config) {
        ConfigurationSection regicide = config.createSection("regicide");
        regicide.set("attack-king-flat", regicideAttackFlatKing);
        regicide.set("attack-king-mult", regicideAttackMultiplicativeKing);
        regicide.set("attack-others-flat", regicideAttackFlatOthers);
        regicide.set("attack-others-mult", regicideAttackMultiplicativeOthers);
        regicide.set("defend-king-flat", regicideDefendFlatKing);
        regicide.set("defend-king-mult", regicideDefendMultiplicativeKing);
        regicide.set("defend-others-flat", regicideDefendFlatOthers);
        regicide.set("defend-others-mult", regicideDefendMultiplicativeOthers);
        ConfigurationSection stealer = config.createSection("stealer");
        stealer.set("points-per-hit", stealerPointsPerHit);
    }

    @Override
    public boolean isValid() {
        return ( true );
    }

    public static @Nonnull TagsConfigurationSection defaultConfiguration() {
        TagsConfigurationSection config = new TagsConfigurationSection();

        // Regicide
        config.regicideAttackFlatKing = 0;
        config.regicideAttackMultiplicativeKing = 1.2;
        config.regicideAttackFlatOthers = 0;
        config.regicideAttackMultiplicativeOthers = 1.1;
        config.regicideDefendFlatKing = 0;
        config.regicideDefendMultiplicativeKing = 0.8;
        config.regicideDefendFlatOthers = 0;
        config.regicideDefendMultiplicativeOthers = 0.9;

        // Stealer
        config.stealerPointsPerHit = 1;

        return config;
    }

    @Override
    public String nicePrint(String prefix, String last) {
        return "§7{"
                + prefix + "§7regicide = {"
                + prefix + "  §7attack.king.flat = " + niceDouble(regicideAttackFlatKing, 0)
                + prefix + "  §7attack.king.mult = " + niceDouble(regicideAttackMultiplicativeKing, 1)
                + prefix + "  §7attack.others.flat = " + niceDouble(regicideAttackFlatOthers, 0)
                + prefix + "  §7attack.others.mult = " + niceDouble(regicideAttackMultiplicativeOthers, 1)
                + prefix + "  §7defend.king.flat = " + niceDouble(regicideDefendFlatKing, 0)
                + prefix + "  §7defend.king.mult = " + niceDouble(regicideDefendMultiplicativeKing, 1)
                + prefix + "  §7defend.others.flat = " + niceDouble(regicideDefendFlatOthers, 0)
                + prefix + "  §7defend.others.mult = " + niceDouble(regicideDefendMultiplicativeOthers, 1)
                + prefix + "},"
                + prefix + "§7stealer.points-per-hit = " + niceInt(stealerPointsPerHit, 0)
                + last + "§7}";
    }

}
