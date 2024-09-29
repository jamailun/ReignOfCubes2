package fr.jamailun.reignofcubes2.players;

import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.sounds.SoundEffect;
import fr.jamailun.reignofcubes2.messages.Messages;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Abstract implementation with basic access to the underlying Bukkit Player object.
 */
public abstract class BasicRocPlayer implements RocPlayer {

    @Getter protected Player player;
    @Getter @Setter private String language = "fr";

    protected BasicRocPlayer(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public void teleport(@NotNull Location location) {
        if(isValid())
            player.teleport(location);
    }

    @Override
    public void playSound(@NotNull SoundEffect effect) {
        playSound(effect.sound(), effect.volume(), effect.pitch());
    }

    @Override
    public void sendMessage(String entry, Object... args) {
        if(isValid())
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
    public void playSound(@NotNull Sound sound, float volume, float pitch) {
        if(isValid())
            player.playSound(player.getLocation(), sound, volume, pitch);
    }

    @Override
    public @NotNull Location getLocation() {
        return player.getLocation();
    }

    @Override
    public int hashCode() {
        return getUUID().hashCode();
    }
}
