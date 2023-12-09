package fr.jamailun.reignofcubes2.players;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Handle players in the game.
 */
public class PlayersManager implements Iterable<RocPlayer> {

    private final Map<UUID, RocPlayer> players = new HashMap<>();

    public RocPlayer join(Player player) {
        UUID uuid = player.getUniqueId();
        if(players.containsKey(uuid)) {
            return players.get(uuid);
        }
        RocPlayer rp = new RocPlayer(player);
        players.put(uuid, rp);
        return rp;
    }

    public void clear() {
        players.clear();
    }

    public boolean exists(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public @NotNull RocPlayer get(Player player) {
        RocPlayer p = players.get(player.getUniqueId());
        assert p != null : "Got a NULL rocPlayer from vanilla player '" + player.getName() + "'.";
        return p;
    }

    public void broadcast(String message, Object... args) {
        for(RocPlayer player : this) {
            player.sendMessage(message, args);
        }
    }

    @Override
    public @NotNull Iterator<RocPlayer> iterator() {
        return players.values().iterator();
    }
}
