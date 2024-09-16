package fr.jamailun.reignofcubes2.configuration;

import fr.jamailun.reignofcubes2.api.configuration.PersistedProperty;
import fr.jamailun.reignofcubes2.api.configuration.PropertiesHolder;
import lombok.Getter;

@Getter
public final class GameRules extends PropertiesHolder {

    @PersistedProperty(section = "players", name = "count-min")
    int playerCountMin = -1;
    @PersistedProperty(section = "players", name = "count-max")
    int playerCountMax = -1;

    @PersistedProperty(section = "score", name = "goal")
    int scoreGoal = -1;
    @PersistedProperty(section = "score", name = "king.per-second")
    int scoreKingPerSecond = -1;
    @PersistedProperty(section = "score", name = "king.bonus")
    int scoreKingBonus = -1;
    @PersistedProperty(section = "score", name = "kill.bonus-flat")
    int scoreKillFlat = -1;
    @PersistedProperty(section = "score", name = "kill.bonus-steal")
    double scoreKillSteal = -1;
    @PersistedProperty(section = "score", name = "king.death-penalty")
    int scoreDeathPenalty = -1;

    @PersistedProperty(section = "throne", name = "duration.normal")
    double crownDuration = -1;
    @PersistedProperty(section = "throne", name = "duration.steal")
    double crownDurationSteal = -1;
    @PersistedProperty(section = "throne", name = "cooldown")
    double throneCooldown = -1;

    @PersistedProperty(section = "mine", name = "king.death-penalty")
    double mineCaptureDuration = -1;

    @PersistedProperty(section = "general", name = "kill-on-y")
    double killY = -1;
    @PersistedProperty(section = "general", name = "spawn-safe-distance")
    double spawnSafeDistance = -1;

    @Override
    public boolean isPlayable() {
        // players
        return (playerCountMin > 0 && playerCountMax > playerCountMin)
                // Score
                && (scoreGoal > 0 && scoreKingPerSecond >= 0 && scoreKingBonus >= 0 &&
                scoreKillFlat >= 0 && scoreKillSteal >= 0 && scoreDeathPenalty >= 0)
                // Throne
                && (crownDuration > 0 && crownDurationSteal > 0 && throneCooldown >= 0)
                // Mine
                && (mineCaptureDuration > 1)
                // General
                && (spawnSafeDistance >= -1)
                ;
    }
}
