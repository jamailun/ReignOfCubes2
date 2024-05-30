package fr.jamailun.reignofcubes2.api.players;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;

public enum ScoreAddReason {

    KING_FLAT_BONUS("score.reason.king-bonus"),
    KING_EVERY_SECOND(null),
    KILL_FLAT("score.reason.kill-bonus"),
    KILL_STEAL("score.reason.kill-steal"),
    PICKUP("score.reason.pickup"),
    ADMINISTRATOR("score.reason.administrator"),


    TAG_STEALER("score.reason.tags.stolen");

    private final String entry;
    ScoreAddReason(String entry) {
        this.entry = entry;
    }

    public String toString(String language) {
        if(entry == null) return null;
        return ReignOfCubes2.getI18n(language, entry);
    }

    public boolean hasMessage() {
        return entry != null;
    }

}
