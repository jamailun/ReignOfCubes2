package fr.jamailun.reignofcubes2.players;

import fr.jamailun.reignofcubes2.RocScheduler;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.api.events.player.PlayerScoreLostEvent;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.players.ScoreAddReason;
import fr.jamailun.reignofcubes2.api.players.ScoreRemoveReason;
import fr.jamailun.reignofcubes2.api.tags.RocTag;
import fr.jamailun.reignofcubes2.api.events.player.PlayerScoreGainedEvent;
import fr.jamailun.reignofcubes2.music.SoundsLibrary;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Wrap players for the RoC game.
 */

public class RocPlayerImpl extends BasicRocPlayer {

    @Getter private int score = 0;
    @Getter private float gold = 0;
    @Getter @Setter private boolean isKing = false;
    @Getter @Setter private String language = "fr";
    @Getter private int lastMoneySpent = 0;
    private RocPlayer lastDamager;
    private long lastDamageTook = 0;

    private RocTag tag;

    public RocPlayerImpl(@NotNull Player player) {
        super(player);
    }

    @Override
    public void addScore(int delta, @NotNull ScoreAddReason reason) {
        if(delta == 0) return;
        assert delta > 0;

        // Event propagation
        PlayerScoreGainedEvent event = new PlayerScoreGainedEvent(this, delta, reason);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;

        // Delta
        score += delta;
        if(reason.hasMessage())
            sendMessage("score.base.gain", String.valueOf(delta), reason.toString(language));
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

        // Event propagation
        PlayerScoreLostEvent event = new PlayerScoreLostEvent(this, delta, reason);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;

        // Delta
        score = Math.max(0, score - delta);
        if(reason.hasMessage())
            sendMessage("score.base.loose", String.valueOf(delta), reason.toString(language));
    }

    @Override
    public void buyKit(@NotNull Kit kit) {
        kit.equip(this);
        // Effects
        playSound(SoundsLibrary.KIT_BOUGHT);
        removeScore(kit.getCost(), ScoreRemoveReason.BUY_KIT);
        sendMessage("score.messages.kit-bought");
    }

    @Override
    public boolean hasGold(float value) {
        return gold >= value;
    }

    @Override
    public void addGold(float delta) {
        if(delta == 0) return;
        assert delta > 0;
        gold += delta;
    }

    @Override
    public void removeGold(float delta) {
        if(delta == 0) return;
        assert delta > 0;
        gold = Math.max(0, gold - delta);
    }

    @Override
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

        // Equip default kit
        Kit defaultKit = ReignOfCubes2.kits().getDefaultKit();
        if(defaultKit == null) {
            ReignOfCubes2.logger().error("No default kit !");
        } else {
            defaultKit.equip(this);
        }

        // Add shop item
        ItemStack is = ReignOfCubes2.game().getActiveConfiguration().getShopItem();
        if (is != null) {
            getPlayer().getInventory().setItem(8, is);
        }

        // Heal (after small delay, in case of respawn).
        RocScheduler.runTaskLater(() -> {
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
            return Objects.equals(rp.getUUID(), getUUID());
        }
        return false;
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

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public void resetAndGoToLobby() {
        if(ReignOfCubes2.isLobbySet())
            teleport(ReignOfCubes2.getLobby());

        player.setGameMode(GameMode.ADVENTURE);
        player.setSaturation(20);
        player.setFoodLevel(20);
        player.getInventory().clear();

        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
    }
}
