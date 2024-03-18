package fr.jamailun.reignofcubes2.players;

import fr.jamailun.reignofcubes2.messages.Messages;

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
        return Messages.get(language, entry);
    }

    public boolean hasMessage() {
        return entry != null;
    }

}
