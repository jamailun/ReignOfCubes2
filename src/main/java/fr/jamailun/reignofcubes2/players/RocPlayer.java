package fr.jamailun.reignofcubes2.players;

import fr.jamailun.reignofcubes2.events.ScoreGainedEvent;
import fr.jamailun.reignofcubes2.messages.Messages;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Wrap players for the RoC game.
 */
@Getter
public class RocPlayer {

    private final Player player;

    private int score = 0;
    @Setter private boolean isKing = false;
    @Setter private String language = "fr";

    @Setter private RocPlayer lastDamager;

    public RocPlayer(Player player) {
        this.player = player;
    }

    public void addScore(int delta, ScoreAddReason reason) {
        if(delta == 0) return;
        assert delta > 0;

        score += delta;
        if(reason.hasMessage())
            sendMessage("score.base.gain", String.valueOf(delta), reason.toString(language));

        ScoreGainedEvent event = new ScoreGainedEvent(this, delta, reason);
        Bukkit.getPluginManager().callEvent(event);
    }

    public boolean hasScore(int score) {
        assert score >= 0;
        return this.score >= score;
    }

    public void removeScore(int delta, ScoreRemoveReason reason) {
        if(delta == 0) return;
        assert delta > 0;

        score = Math.max(0, score - delta);
        if(reason.hasMessage())
            sendMessage("score.base.loose", String.valueOf(delta), reason.toString(language));
    }

    public void sendMessage(String entry, Object... args) {
        Messages.send(player, language, entry, args);
    }

    public String i18n(String entry, Object... args) {
        return Messages.format(language, entry, args);
    }

    public UUID getUUID() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getName();
    }

    public boolean isValid() {
        return player.isValid() && ! player.isDead() && player.isOnline();
    }

    public void reset() {
        score = 0;
        isKing = false;
        lastDamager = null;
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
        player.setSaturation(100);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(obj instanceof RocPlayer rp) {
            return rp.getUUID().equals(getUUID());
        }
        return false;
    }
}
