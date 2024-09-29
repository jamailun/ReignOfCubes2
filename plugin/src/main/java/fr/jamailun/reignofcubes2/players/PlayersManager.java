package fr.jamailun.reignofcubes2.players;

import fr.jamailun.reignofcubes2.api.GameState;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.music.SoundsLibrary;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * Handle players in the game.
 */
public class PlayersManager implements Iterable<RocPlayerImpl> {

    private final Map<UUID, RocPlayerImpl> players = new HashMap<>();

    /**
     * Make a bukkit player join the server.
     * @param player the bukkit player instance.
     * @return the existing RocPlayer instance if player already joined, a new instance if not.
     */
    public @NotNull RocPlayerImpl join(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        if(players.containsKey(uuid)) {
            return players.get(uuid);
        }
        RocPlayerImpl rp = new RocPlayerImpl(player);
        players.put(uuid, rp);
        return rp;
    }

    public void playerLeft(@NotNull Player player) {
        // Only remove when NOT playing !
        if(ReignOfCubes2.state() != GameState.PLAYING) {
            players.remove(player.getUniqueId());
        }
    }

    public void clearOfflinePlayers() {
        for(RocPlayerImpl player : new ArrayList<>(players.values())) {
            if( ! player.isValid()) {
                players.remove(player.getUUID());
                ReignOfCubes2.logger().info("Removed '" + player.getName() + "' from players because he was offline.");
            }
        }
    }

    public boolean exists(@NotNull Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public int size() {
        return players.size();
    }

    public List<RocPlayer> listOnline() {
        return players.values().stream()
                .filter(p -> p.isValid() && ! p.isSpectator())
                .map(RocPlayer.class::cast)
                .toList();
    }

    public @NotNull RocPlayerImpl get(@NotNull Player player) {
        RocPlayerImpl p = players.get(player.getUniqueId());
        assert p != null : "Got a NULL rocPlayer from vanilla player '" + player.getName() + "'.";
        return p;
    }

    public void broadcast(@NotNull String message, @NotNull Object... args) {
        forEach(p -> p.sendMessage(message, args));
    }

    public void playSound(@NotNull SoundsLibrary.SoundEntry sound) {
        forEach(p -> p.playSound(sound));
    }

    public @NotNull @UnmodifiableView Collection<RocPlayer> list() {
        return Collections.unmodifiableCollection(players.values());
    }

    @Override
    public @NotNull Iterator<RocPlayerImpl> iterator() {
        return players.values().iterator();
    }

}
