package fr.jamailun.reignofcubes2.api.players;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;

public record ScoreAddReason(String entry) {

    public static final ScoreAddReason KING_FLAT_BONUS = new ScoreAddReason("score.reason.king-bonus");
    public static final ScoreAddReason KING_EVERY_SECOND = new ScoreAddReason(null);
    public static final ScoreAddReason KILL_FLAT = new ScoreAddReason("score.reason.kill-bonus");
    public static final ScoreAddReason KILL_STEAL = new ScoreAddReason("score.reason.kill-steal");
    public static final ScoreAddReason ADMINISTRATOR = new ScoreAddReason("score.reason.administrator");
    public static final ScoreAddReason TAG_STEALER = new ScoreAddReason("score.reason.tags.stolen");

    public String toString(String language) {
        if(entry == null) return null;
        return ReignOfCubes2.i18n(language, entry);
    }

    public boolean hasMessage() {
        return entry != null;
    }

}
