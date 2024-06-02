package fr.jamailun.reignofcubes2.players;

import fr.jamailun.reignofcubes2.GameManagerImpl;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.api.utils.Ranking;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Handle players in the game.
 */
public class PlayersManager implements Iterable<RocPlayerImpl> {

    private final GameManagerImpl game;
    private final Map<UUID, RocPlayerImpl> players = new HashMap<>();

    public PlayersManager(GameManagerImpl game) {
        this.game = game;
    }

    public RocPlayerImpl join(Player player) {
        UUID uuid = player.getUniqueId();
        if(players.containsKey(uuid)) {
            return players.get(uuid);
        }
        RocPlayerImpl rp = new RocPlayerImpl(player);
        players.put(uuid, rp);
        return rp;
    }

    public void maybeLeave(Player player) {
        // Only remove when NOT playing !
        if(game.isStatePlaying())
            return;
        players.remove(player.getUniqueId());
    }

    public void clearOfflines() {
        for(RocPlayerImpl player : new ArrayList<>(players.values())) {
            if( ! player.isValid()) {
                players.remove(player.getUUID());
                ReignOfCubes2.logInfo("Removed '" + player.getName() + "' from players because he was offline.");
            }
        }
    }

    public boolean exists(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public int size() {
        return players.size();
    }

    public @NotNull RocPlayerImpl get(Player player) {
        RocPlayerImpl p = players.get(player.getUniqueId());
        assert p != null : "Got a NULL rocPlayer from vanilla player '" + player.getName() + "'.";
        return p;
    }

    public void broadcast(String message, Object... args) {
        for(RocPlayerImpl player : this) {
            player.sendMessage(message, args);
        }
    }

    @Override
    public @NotNull Iterator<RocPlayerImpl> iterator() {
        return players.values().iterator();
    }

    public void start(List<Location> spawns) {
        assert ! spawns.isEmpty();
        while(spawns.size() < size()) {
            spawns.addAll(new ArrayList<>(spawns));
        }
        Collections.shuffle(spawns);
        Iterator<Location> spawn = spawns.iterator();

        for(RocPlayerImpl player : this) {
            // Reset
            player.reset();
            // Teleport
            player.getPlayer().teleport(spawn.next());
            // Signal respawn (to give the kit)
            player.respawned();
        }
    }

    public void updateRanking(Ranking<RocPlayer> ranking) {
        ranking.update(players.values());
    }

    public void backToLobby() {
        assert game.getActiveConfiguration().isValid();
        Location lobby = game.getActiveConfiguration().getLobby();

        // tp ALL players instead
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(lobby);

            player.setGameMode(GameMode.ADVENTURE);
            player.setSaturation(20);
            player.setFoodLevel(20);
            player.getInventory().clear();

            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        }
    }

}
