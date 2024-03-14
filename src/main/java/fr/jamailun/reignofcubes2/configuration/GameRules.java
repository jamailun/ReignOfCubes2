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
    private double crownDurationSteal = -1;

    private double generatorFrequency = -1;
    private double killY = 0;

    private double spawnSafeDistance = -1;
    private double throneCooldown = -1;

    private int scoreGoal = -1;
    private int scoreKingPerSecond = -1;
    private int scoreKingBonus = -1;
    private int scorePickup = -1;
    private int scoreKillFlat = -1;
    private double scoreKillSteal = -1;
    private int scoreDeathPenalty = -1;

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
            rules.scorePickup = scoring.getInt("pickup", DEFAULT);
        }

        return rules;
    }

    public void write(ConfigurationSection config) {
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
        scoring.set("pickup", scorePickup);
    }

    public boolean isValid() {
        return (playerCountMin > 0 && playerCountMax > playerCountMin)
                && (crownDuration > 0 && crownDurationSteal > 0)
                && (spawnSafeDistance >= 0 && throneCooldown >= 0)
                && (generatorFrequency > 1)
                && (
                    scoreGoal > 0 && scoreKingPerSecond >= 0 && scoreKingBonus >= 0 &&
                    scoreKillFlat >= 0 && scoreKillSteal >= 0 && scoreDeathPenalty >= 0 &&
                    scorePickup >= 0
                );
    }

    public static GameRules defaultRules() {
        GameRules rules = new GameRules();
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
        rules.scorePickup = 50;
        return rules;
    }

    public String nicePrint(String prefix, String last) {
        return "§7{"
                + prefix + "§7players = " + niceInt(playerCountMin) + " -> " + niceInt(playerCountMax)
                + prefix + "§7crown-duration = " + niceDouble(crownDuration) + "§7, when stealing: " + niceDouble(crownDurationSteal)
                + prefix + "§7spawn.safe-distance = " + niceDouble(spawnSafeDistance)
                + prefix + "§7spawn.throne-cooldown = " + niceDouble(throneCooldown)
                + prefix + "§7generator-frequency = " + niceDouble(generatorFrequency)
                + prefix + "§7kill-Y = §e" + killY
                + prefix + "§7scoring.goal = " + niceInt(scoreGoal)
                + prefix + "§7scoring.king = +" + niceInt(scoreKingBonus) + "§7, per-second: " + niceInt(scoreKingPerSecond)
                + prefix + "§7scoring.kill = +" + niceInt(scoreKillFlat) + "§7, and steal: " + niceDouble(scoreKillSteal)
                + prefix + "§7scoring.death-penalty = " + niceInt(scoreDeathPenalty)
                + prefix + "§7scoring.pickup = " + niceInt(scorePickup)
                + last + "§7}";
    }

    private String niceInt(int i) {
        if(i < 0)
            return "§c" + "<unset>";
        if(i == 0)
            return "§f0";
        return "§a" + i;
    }
    private String niceDouble(double d) {
        if(d < 0)
            return "§c" + "<unset>";
        if(d == 0)
            return "§f0";
        return "§a" + d;
    }

}
