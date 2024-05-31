package fr.jamailun.reignofcubes2.players;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.players.ScoreAddReason;
import fr.jamailun.reignofcubes2.api.players.ScoreRemoveReason;
import fr.jamailun.reignofcubes2.api.sounds.SoundEffect;
import fr.jamailun.reignofcubes2.api.tags.RocTag;
import fr.jamailun.reignofcubes2.configuration.kits.RocKit;
import fr.jamailun.reignofcubes2.api.events.player.ScoreGainedEvent;
import fr.jamailun.reignofcubes2.messages.Messages;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Wrap players for the RoC game.
 */

public class RocPlayerImpl implements RocPlayer {

    @Getter private Player player;

    @Getter private int score = 0;
    @Getter private int gold = 0;
    @Getter @Setter private boolean isKing = false;
    @Getter @Setter private String language = "fr";
    @Getter @Setter private int lastMoneySpent = 0;
    private RocPlayer lastDamager;
    private long lastDamageTook = 0;

    private RocTag tag;

    public RocPlayerImpl(Player player) {
        this.player = player;
    }

    @Override
    public void addScore(int delta, @NotNull ScoreAddReason reason) {
        if(delta == 0) return;
        assert delta > 0;

        score += delta;
        if(reason.hasMessage())
            sendMessage("score.base.gain", String.valueOf(delta), reason.toString(language));

        ScoreGainedEvent event = new ScoreGainedEvent(this, delta, reason);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public boolean hasScore(int score) {
        assert score >= 0;
        return this.score >= score;
    }

    @Override
    public void removeScore(int delta, @NotNull ScoreRemoveReason reason) {
        if(delta == 0) return;
        assert delta > 0;

        score = Math.max(0, score - delta);
        if(reason.hasMessage())
            sendMessage("score.base.loose", String.valueOf(delta), reason.toString(language));
    }

    @Override
    public boolean hasGold(int value) {
        return gold >= value;
    }

    @Override
    public void addGold(int delta) {
        if(delta == 0) return;
        assert delta > 0;
        gold += delta;
    }

    @Override
    public void removeGold(int delta) {
        if(delta == 0) return;
        assert delta > 0;
        gold = Math.max(0, gold - delta);
    }

    @Override
    public void playSound(@NotNull SoundEffect effect) {
        playSound(effect.sound(), effect.volume(), effect.pitch());
    }

    @Override
    public void sendMessage(String entry, Object... args) {
        Messages.send(player, language, entry, args);
    }

    public String i18n(String entry, Object... args) {
        return Messages.format(language, entry, args);
    }

    @Override
    public @NotNull UUID getUUID() {
        return player.getUniqueId();
    }

    @Override
    public @NotNull String getName() {
        return player.getName();
    }

    @Override
    public boolean isValid() {
        return player.isValid() && player.isOnline();
    }

    @Override
    public void teleport(@NotNull Location location) {
        player.teleport(location);
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
        player.clearActivePotionEffects();
        clearTag();
    }

    public void respawned() {
        lastMoneySpent = 0;
        lastDamager = null;
        player.clearActivePotionEffects();

        // Equip default kit
        Kit defaultKit = ReignOfCubes2.getKits().getDefaultKit();
        if(defaultKit == null) {
            ReignOfCubes2.logError("No default kit !");
        } else {
            defaultKit.equip(this);
        }

        // Add shop item
        ItemStack is = MainROC2.getCurrentConfig().getShopItem();
        if (is != null) {
            getPlayer().getInventory().setItem(8, is);
        }

        // Heal
        MainROC2.runTaskLater(() -> {
            player.updateInventory();
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        }, 0.5);
    }

    @Override
    public void setLastDamager(@NotNull RocPlayer damager) {
        lastDamager = damager;
        lastDamageTook = System.currentTimeMillis();
    }

    @Override
    public @Nullable RocPlayer getLastDamager() {
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
        if(obj instanceof RocPlayerImpl rp) {
            return rp.getUUID().equals(getUUID());
        }
        return false;
    }

    public void playSound(@NotNull Sound sound, float volume, float pitch) {
        if(!isValid()) return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void changePlayerInstance(Player player) {
        this.player = player;
    }

    @Override
    public void setTag(@Nullable RocTag tag) {
        clearTag();
        this.tag = tag;
        if(tag != null)
            tag.playerAdded(this);
    }

    @Override
    public void clearTag() {
        if(hasTag()) {
            tag.playerRemoved(this);
            tag = null;
        }
    }

    @Override
    public boolean hasTag() {
        return tag != null;
    }

    @Override
    public Optional<RocTag> getTag() {
        return Optional.ofNullable(tag);
    }

}
