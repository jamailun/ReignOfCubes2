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
 * Set of rules for the game.
 */
@Getter
@Setter
public class GameRulesSection extends RocConfigurationSection {

    private static final int DEFAULT = -1;

    private int playerCountMin = -1;
    private int playerCountMax = -1;

    private double crownDuration = -1;
    private double crownDurationSteal = -1;

    private double generatorFrequency = -1;
    private double killY = 0;

    private double spawnSafeDistance = -1;
    private double throneCooldown = -1;

    private int scoreGoal = -1;
    private int scoreKingPerSecond = -1;
    private int scoreKingBonus = -1;
    private int scoreKillFlat = -1;
    private double scoreKillSteal = -1;
    private int scoreDeathPenalty = -1;

    @DeserializeConfiguration
    public static @Nonnull GameRulesSection load(@Nullable ConfigurationSection config) {
        if(config == null)
            return defaultRules();

        GameRulesSection rules = new GameRulesSection();
        // Players
        ConfigurationSection playerCount = config.getConfigurationSection("players-count");
        if(playerCount != null) {
            rules.playerCountMin = playerCount.getInt("min", DEFAULT);
            rules.playerCountMax = playerCount.getInt("max", DEFAULT);
        }

        // crown
        rules.crownDuration = config.getDouble("crown-duration", DEFAULT);
        rules.crownDurationSteal = config.getDouble("crown-duration-steal", DEFAULT);

        // miscellaneous
        rules.generatorFrequency = config.getDouble("generator-frequency", DEFAULT);
        rules.killY = config.getDouble("y-kill", 0);

        // other
        rules.spawnSafeDistance = config.getDouble("spawn-safe-distance", DEFAULT);
        rules.throneCooldown = config.getDouble("throne-cooldown", DEFAULT);

        // scoring
        ConfigurationSection scoring = config.getConfigurationSection("scoring");
        if(scoring != null) {
            rules.scoreGoal = scoring.getInt("goal", DEFAULT);
            rules.scoreKingPerSecond = scoring.getInt("king-per-second", DEFAULT);
            rules.scoreKingBonus = scoring.getInt("king-bonus", DEFAULT);
            rules.scoreKillFlat = scoring.getInt("kill-flat", DEFAULT);
            rules.scoreKillSteal = scoring.getDouble("kill-steal", DEFAULT);
            rules.scoreDeathPenalty = scoring.getInt("death-penalty", DEFAULT);
        }

        return rules;
    }

    @Override
    public void write(@NotNull ConfigurationSection config) {
        ConfigurationSection pc = config.createSection("players-count");
        pc.set("min", playerCountMin);
        pc.set("max", playerCountMax);

        config.set("crown-duration", crownDuration);
        config.set("crown-duration-steal", crownDurationSteal);

        config.set("generator-frequency", generatorFrequency);
        config.set("y-kill", killY);

        config.set("spawn-safe-distance", spawnSafeDistance);
        config.set("throne-cooldown", throneCooldown);

        ConfigurationSection scoring = config.createSection("scoring");
        scoring.set("goal", scoreGoal);
        scoring.set("king-per-second", scoreKingPerSecond);
        scoring.set("king-bonus", scoreKingBonus);
        scoring.set("kill-flat", scoreKillFlat);
        scoring.set("kill-steal", scoreKillSteal);
        scoring.set("death-penalty", scoreDeathPenalty);
    }

    @Override
    public boolean isValid() {
        return (playerCountMin > 0 && playerCountMax > playerCountMin)
                && (crownDuration > 0 && crownDurationSteal > 0)
                && (spawnSafeDistance >= 0 && throneCooldown >= 0)
                && (generatorFrequency > 1)
                && (
                    scoreGoal > 0 && scoreKingPerSecond >= 0 && scoreKingBonus >= 0 &&
                    scoreKillFlat >= 0 && scoreKillSteal >= 0 && scoreDeathPenalty >= 0
                );
    }

    /**
     * Create a new GameRules set, with default playable values.
     * @return an invalid GamesRules. Spawn, lobby, ... must be set.
     */
    public static @Nonnull GameRulesSection defaultRules() {
        GameRulesSection rules = new GameRulesSection();
        // players
        rules.playerCountMin = 4;
        rules.playerCountMax = 32;
        // crown
        rules.crownDuration = 25;
        rules.crownDurationSteal = 60;
        // Others
        rules.spawnSafeDistance = 12;
        rules.throneCooldown = 1.5;
        rules.generatorFrequency = 30;
        rules.killY = 0;
        // score
        rules.scoreGoal = 1000;
        rules.scoreKingBonus = 80;
        rules.scoreKingPerSecond = 1;
        rules.scoreKillSteal = 0.5d;
        rules.scoreKillFlat = 50;
        rules.scoreDeathPenalty = 20;
        return rules;
    }

    @Override
    public String nicePrint(String prefix, String last) {
        return "§7{"
                + prefix + "§7players = " + niceInt(playerCountMin, 0) + " -> " + niceInt(playerCountMax, 0)
                + prefix + "§7crown-duration = " + niceDouble(crownDuration, 0) + "§7, when stealing: " + niceDouble(crownDurationSteal, 0)
                + prefix + "§7spawn.safe-distance = " + niceDouble(spawnSafeDistance, 0)
                + prefix + "§7spawn.throne-cooldown = " + niceDouble(throneCooldown, 0)
                + prefix + "§7generator-frequency = " + niceDouble(generatorFrequency, 0)
                + prefix + "§7kill-Y = §e" + killY
                + prefix + "§7scoring.goal = " + niceInt(scoreGoal, 0)
                + prefix + "§7scoring.king = +" + niceInt(scoreKingBonus, 0) + "§7, per-second: " + niceInt(scoreKingPerSecond, 0)
                + prefix + "§7scoring.kill = +" + niceInt(scoreKillFlat, 0) + "§7, and steal: " + niceDouble(scoreKillSteal, 0)
                + prefix + "§7scoring.death-penalty = " + niceInt(scoreDeathPenalty, 0)
                + last + "§7}";
    }

}
