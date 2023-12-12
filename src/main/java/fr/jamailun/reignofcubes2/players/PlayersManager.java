package fr.jamailun.reignofcubes2.players;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.utils.Ranking;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Handle players in the game.
 */
public class PlayersManager implements Iterable<RocPlayer> {

    private final GameManager game;
    private final Map<UUID, RocPlayer> players = new HashMap<>();

    public PlayersManager(GameManager game) {
        this.game = game;
    }

    public RocPlayer join(Player player) {
        UUID uuid = player.getUniqueId();
        if(players.containsKey(uuid)) {
            return players.get(uuid);
        }
        RocPlayer rp = new RocPlayer(player);
        players.put(uuid, rp);
        return rp;
    }

    public void maybeLeave(Player player) {
        // Only remove when NOT playing !
        if(game.isPlaying())
            return;
        players.remove(player.getUniqueId());
    }

    public void clearOfflines() {
        for(RocPlayer player : new ArrayList<>(players.values())) {
            if( ! player.isValid()) {
                players.remove(player.getUUID());
                ReignOfCubes2.info("Removed '" + player.getName() + "' from players because he was offline.");
            }
        }
    }

    public boolean exists(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public int size() {
        return players.size();
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

    public void start(List<Location> spawns) {
        assert ! spawns.isEmpty();
        while(spawns.size() < size()) {
            spawns.addAll(new ArrayList<>(spawns));
        }
        Collections.shuffle(spawns);
        Iterator<Location> spawn = spawns.iterator();

        for(RocPlayer player : this) {
            // Reset
            player.reset();
            // Teleport
            player.getPlayer().teleport(spawn.next());
        }
    }

    public void updateRanking(Ranking<RocPlayer> ranking) {
        ranking.update(players.values());
    }
}
