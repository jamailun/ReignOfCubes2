package fr.jamailun.reignofcubes2.configuration;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Getter
@Setter
public class GameRules {

    private static final int DEFAULT = -1;

    private int playerCountMin = -1;
    private int playerCountMax = -1;

    private double crownDuration = -1;

    private int scoreGoal = -1;
    private int scoreKingPerSecond = -1;
    private int scoreKingBonus = -1;
    private int scoreKillFlat = -1;
    private double scoreKillSteal = -1;
    private int scoreDeathPenalty = -1;

    public GameRules() {}

    public static @Nonnull GameRules load(@Nullable ConfigurationSection config) {
        if(config == null)
            return defaultRules();

        GameRules rules = new GameRules();
        // Players
        ConfigurationSection playerCount = config.getConfigurationSection("players-count");
        if(playerCount != null) {
            rules.playerCountMin = playerCount.getInt("min", DEFAULT);
            rules.playerCountMax = playerCount.getInt("max", DEFAULT);
        }

        // crown
        rules.crownDuration = config.getDouble("crown-duration", DEFAULT);

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

    public void write(ConfigurationSection config) {
        ConfigurationSection pc = config.createSection("players-count");
        pc.set("min", playerCountMin);
        pc.set("max", playerCountMax);

        config.set("crown-duration", crownDuration);

        ConfigurationSection scoring = config.createSection("scoring");
        scoring.set("goal", scoreGoal);
        scoring.set("king-per-second", scoreKingPerSecond);
        scoring.set("king-bonus", scoreKingBonus);
        scoring.set("kill-flat", scoreKillFlat);
        scoring.set("kill-steal", scoreKillSteal);
        scoring.set("death-penalty", scoreDeathPenalty);
    }

    public boolean isValid() {
        return (playerCountMin > 0 && playerCountMax > playerCountMin)
                && crownDuration > 0
                && (
                    scoreGoal > 0 && scoreKingPerSecond >= 0 && scoreKingBonus >= 0
                && scoreKillFlat >= 0 && scoreKillSteal >= 0 && scoreDeathPenalty >= 0
                );
    }

    public static GameRules defaultRules() {
        GameRules rules = new GameRules();
        // players
        rules.playerCountMin = 4;
        rules.playerCountMax = 32;
        // crown
        rules.crownDuration = 25;
        // score
        rules.scoreGoal = 1000;
        rules.scoreKingBonus = 50;
        rules.scoreKingPerSecond = 1;
        rules.scoreKillSteal = 0.5d;
        rules.scoreKillFlat = 50;
        rules.scoreDeathPenalty = 20;
        return rules;
    }

}
