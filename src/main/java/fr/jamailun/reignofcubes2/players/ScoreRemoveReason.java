package fr.jamailun.reignofcubes2.players;

import fr.jamailun.reignofcubes2.messages.Messages;

public enum ScoreRemoveReason {

    BUY_KIT("score.buy-kit"),
    DEATH_PENALTY("score.death-penalty"),
    KILL_STEAL("score.kill-steal");

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
