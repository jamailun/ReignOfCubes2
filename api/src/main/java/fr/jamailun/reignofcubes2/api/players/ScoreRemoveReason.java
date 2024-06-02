package fr.jamailun.reignofcubes2.api.players;

import fr.jamailun.reignofcubes2.api.ReignOfCubes2;

public record ScoreRemoveReason(String entry) {

    public static final ScoreRemoveReason BUY_KIT = new ScoreRemoveReason("score.reason.buy-kit");
    public static final ScoreRemoveReason DEATH_PENALTY = new ScoreRemoveReason("score.reason.death-penalty");
    public static final ScoreRemoveReason KILL_STEAL = new ScoreRemoveReason("score.reason.kill-steal");
    public static final ScoreRemoveReason ADMINISTRATOR = new ScoreRemoveReason("score.reason.administrator");
    public static final ScoreRemoveReason TAG_STEALER = new ScoreRemoveReason("score.reason.tags.steal");

    public String toString(String language) {
        if(entry == null) return null;
        return ReignOfCubes2.i18n(language, entry);
    }

    public boolean hasMessage() {
        return entry != null;
    }

}
