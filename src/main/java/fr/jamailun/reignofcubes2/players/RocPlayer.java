package fr.jamailun.reignofcubes2.players;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * Wrap players for the RoC game.
 */
public class RocPlayer {

    @Getter private final Player player;

    @Getter private int score = 0;
    @Getter @Setter private boolean isKing = false;
    @Getter @Setter private Messages.Language language = Messages.Language.FR;

    public RocPlayer(Player player) {
        this.player = player;
    }

    public void gainScore(int delta) {
        if(delta == 0) return;

        assert delta > 0;
        score += delta;
    }

    public boolean hasScore(int score) {
        assert score >= 0;
        return this.score >= score;
    }

    public void spendScore(int delta) {
        if(delta == 0) return;

        assert delta > 0;
        score = Math.max(0, score - delta);
    }

    public void sendMessage(String entry, Object... args) {
        Messages.send(player, language, entry, args);
    }

    public String getName() {
        return player.getName();
    }

}
