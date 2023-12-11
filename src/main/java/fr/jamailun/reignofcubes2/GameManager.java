package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.configuration.ConfigurationsList;
import fr.jamailun.reignofcubes2.configuration.GameRules;
import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import fr.jamailun.reignofcubes2.objects.Throne;
import fr.jamailun.reignofcubes2.players.PlayersManager;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GameManager {

    private final PlayersManager players = new PlayersManager();

    @Getter private GameState state;
    @Getter private final ConfigurationsList configurationsList = new ConfigurationsList();
    @Getter private WorldConfiguration worldConfiguration;
    private World world;
    @Getter private RocPlayer king;
    @Getter private Throne throne;

    GameManager() {
        loadConfiguration(configurationsList.getDefault());
    }

    public boolean loadConfiguration(@Nullable WorldConfiguration configuration) {
        if(isPlaying()) {
            ReignOfCubes2.error("Cannot change configuration while playing !");
            return false;
        }
        if(configuration == null) {
            ReignOfCubes2.warning("Setting GameManager configuration as null.");
            worldConfiguration = null;
            if(throne != null) {
                throne.reset();
                throne = null;
            }
            world = null;
            state = GameState.NOT_CONFIGURED;
            return true;
        }
        if(!configuration.isValid()) {
            ReignOfCubes2.error("Could not load invalid configuration " + configuration);
            return false;
        }
        worldConfiguration = configuration;
        throne = worldConfiguration.generateThrone(this);
        world = Bukkit.getWorld(worldConfiguration.getWorldName());
        state = GameState.WAITING;
        return true;
    }

    public void playerJoinsServer(Player p) {
        RocPlayer player = players.join(p);
        broadcast("event.joined", p.getName());
        player.sendMessage("event.joined-direct");

        testShouldStartGame();
    }

    public void playerLeftServer(Player p) {
        if(!players.exists(p)) {
            return;
        }
        RocPlayer player = players.get(p);
        if(player.isKing()) {
            // not anymore
            players.broadcast("event.left-king", p.getName());
            player.setKing(false);
            setKing(null);
        } else {
            players.broadcast("event.left", p.getName());
        }
    }

    private void testShouldStartGame() {
        if(isPlaying()) return;
        if(worldConfiguration == null || ! worldConfiguration.isValid()) return;
        int minPlayers = getRules().getPlayerCountMin();
        if(players.size() >= minPlayers) {
            ReignOfCubes2.info("Enough players ! Will start the game now.");
            start();
        }
    }

    public void playerDies(@Nonnull RocPlayer victim) {
        RocPlayer killer = victim.getLastDamager();
        if(killer == null) {
            broadcast("event.death.alone", victim.getName());
            //players.deathPenalty();
            return;
        }
        broadcast("event.death.killed", victim.getName(), killer.getName());

        //TODO death logic
        // - points
        // - death of the king
        // - respawn
    }

    private void setKing(RocPlayer player) {
        if(player == null) {
            if(king != null) {
                ReignOfCubes2.info("King has been set as null.");
                broadcast("event.king.death", king.getName());
                king.setKing(false);
                king = null;
            }
            return;
        }
        // remove king from old king.
        if(king != null)
            king.setKing(false);

        // set new king
        player.setKing(true);
        king = player;
        broadcast("event.king.new", king.getName());
    }

    public boolean isInWorld(World w) {
        if(state == GameState.NOT_CONFIGURED)
            return false;
        return this.world.equals(w);
    }

    public boolean isPlaying() {
        return state == GameState.PLAYING;
    }

    public @Nullable RocPlayer toPlayer(@Nullable Player p) {
        if(p == null) return null;
        if(isPlaying()) {
            if(players.exists(p))
                return players.get(p);
            return null;
        }
        return players.join(p);
    }

    public boolean hasKing() {
        return king != null;
    }

    public void ceremonyIsOver(RocPlayer player) {
        assert king == null : "How can a ceremony be ver if there is already a king ?";
        player.sendMessage("throne.end-ceremony");
        setKing(player);
    }

    public GameRules getRules() {
        return worldConfiguration.getRules();
    }

    public void start() {
        assert state == GameState.WAITING;
        assert worldConfiguration != null && worldConfiguration.isValid();
        // 1) tp players to a spawn-point.
        players.start(worldConfiguration.generateSpawns());
        // 2) set state
        state = GameState.PLAYING;

        //TODO message
        Bukkit.broadcastMessage("§6§l > game started.");
    }

    public void stop() {
        if(!isPlaying()) {
            ReignOfCubes2.warning("Tried to stop the game... Was already the case.");
            return;
        }

        //TODO cancel the game.
    }

    public void broadcast(String entry, Object... args) {
        players.broadcast(entry, args);
    }

    public int getPlayersCount() {
        return players.size();
    }

}
