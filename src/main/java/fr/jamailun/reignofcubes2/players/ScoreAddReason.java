package fr.jamailun.reignofcubes2.players;

import fr.jamailun.reignofcubes2.messages.Messages;

public enum ScoreAddReason {

    KING_FLAT_BONUS("score.king-bonus"),
    KING_EVERY_SECOND(null),
    KILL_FLAT("score.kill-bonus"),
    KILL_STEAL("score.kill-steal");

    private final String entry;
    ScoreAddReason(String entry) {
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
