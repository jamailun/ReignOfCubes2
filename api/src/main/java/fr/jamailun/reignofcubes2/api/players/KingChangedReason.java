package fr.jamailun.reignofcubes2.api.players;

public record KingChangedReason() {

    public static final KingChangedReason CROWNING_ON_THRONE = new KingChangedReason();
    public static final KingChangedReason OLD_KING_KILLED = new KingChangedReason();
    public static final KingChangedReason OLD_KING_DIED_ALONE = new KingChangedReason();
    public static final KingChangedReason OLD_KING_LEFT = new KingChangedReason();
    public static final KingChangedReason ADMINISTRATOR = new KingChangedReason();

}
