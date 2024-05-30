package fr.jamailun.reignofcubes2.api.players.statistics;

import java.time.Duration;

public interface TotalPlayerStatistics {

    Duration getPlayedDuration();

    Duration getOnlineDuration();

    long getTotalKillCount();

    long getTotalDeathCount();

    int getGamesCountPlayed();

    int getGamesCountWon();

    long getTotalGoldGained();

    long getTotalScoreGained();

    long getTotalKitBought();

}
