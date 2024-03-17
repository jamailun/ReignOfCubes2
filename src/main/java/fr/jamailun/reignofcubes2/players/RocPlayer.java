package fr.jamailun.reignofcubes2.players;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.SoundsLibrary;
import fr.jamailun.reignofcubes2.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.events.ScoreGainedEvent;
import fr.jamailun.reignofcubes2.messages.Messages;
import fr.jamailun.reignofcubes2.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Wrap players for the RoC game.
 */

public class RocPlayer {

    @Getter private Player player;

    @Getter private int score = 0;
    @Getter @Setter private boolean isKing = false;
    @Getter @Setter private String language = "fr";
    @Getter @Setter private int lastMoneySpent = 0;
    private RocPlayer lastDamager;
    private long lastDamageTook = 0;

    private Tag tag;

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
        return player.isValid() && player.isOnline();
    }

    public void reset() {
        score = 0;
        lastMoneySpent = 0;
        isKing = false;
        lastDamager = null;
        player.closeInventory();
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        player.setSaturation(20);
    }

    public void respawned() {
        lastMoneySpent = 0;
        lastDamager = null;

        // Equip default kit
        Kit defaultKit = ReignOfCubes2.getKits().getDefaultKit();
        if(defaultKit == null) {
            ReignOfCubes2.error("No default kit !");
        } else {
            defaultKit.equip(this);
        }

        // Add shop item
        ItemStack is = ReignOfCubes2.getCurrentConfig().getShopItem();
        if (is != null) {
            getPlayer().getInventory().setItem(8, is);
        }

        // Heal
        ReignOfCubes2.runTaskLater(() -> {
            player.updateInventory();
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        }, 0.5);
    }

    public void setLastDamager(RocPlayer damager) {
        lastDamager = damager;
        lastDamageTook = System.currentTimeMillis();
    }

    public RocPlayer getLastDamager() {
        long now = System.currentTimeMillis();
        if(now - lastDamageTook <= 6000) { // last 6 seconds ?
            return lastDamager;
        }
        return null;
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

    public void playSound(Sound sound, float volume, float pitch) {
        if(!isValid()) return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void changePlayerInstance(Player player) {
        this.player = player;
    }

    public void playSound(SoundsLibrary.SoundEntry entry) {
        playSound(entry.sound(), 5f, entry.pitch());
    }

    public void setTag(@NotNull Tag tag) {
        clearTag();
        this.tag = tag;
        tag.added(this);
    }

    public void clearTag() {
        if(hasTag()) {
            tag.removed(this);
            tag = null;
        }
    }

    public boolean hasTag() {
        return tag != null;
    }

    public Optional<Tag> getTag() {
        return Optional.ofNullable(tag);
    }

}
