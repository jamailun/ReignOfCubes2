package fr.jamailun.reignofcubes2.api.players;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;

public enum ScoreRemoveReason {

    BUY_KIT("score.reason.buy-kit"),
    DEATH_PENALTY("score.reason.death-penalty"),
    KILL_STEAL("score.reason.kill-steal"),
    ADMINISTRATOR("score.reason.administrator"),

    TAG_STEALER("score.reason.tags.steal");

    private final String entry;
    ScoreRemoveReason(String entry) {
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
