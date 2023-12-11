package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.configuration.ConfigurationsList;
import fr.jamailun.reignofcubes2.configuration.GameRules;
import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import fr.jamailun.reignofcubes2.objects.Ceremony;
import fr.jamailun.reignofcubes2.objects.Throne;
import fr.jamailun.reignofcubes2.players.PlayersManager;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

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
    private BukkitTask scoreTimer;

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
            if(victim.isKing()) {
                setKing(null);
                broadcast("event.king.death", victim.getName());
            } else {
                broadcast("event.death.alone", victim.getName());
            }
            victim.removeScore(getRules().getScoreDeathPenalty());
            return;
        }

        // broadcast + king switch
        if(killer.isKing()) {
            broadcast("event.death.killed-as-king", victim.getName(), killer.getName());
            //TODO points bonus if king
        } else if(victim.isKing()) {
            broadcast("event.death.killed-king", victim.getName(), killer.getName());
            setKing(killer);
        } else {
            broadcast("event.death.killed", victim.getName(), killer.getName());
        }

        // Points
        killer.addScore(getRules().getScoreKillFlat());
        int stoleVictimScore = (int) (victim.getScore() * getRules().getScoreKillSteal());
        if(stoleVictimScore > 0) {
            killer.addScore(stoleVictimScore);
            victim.removeScore(stoleVictimScore);
        }
        victim.removeScore(getRules().getScoreDeathPenalty());
    }

    private void setKing(RocPlayer player) {
        if(player == null) {
            if(king != null) {
                broadcast("event.king.death", king.getName());
                king.setKing(false);
                king = null;
            }
            return;
        }
        // remove king from old king.
        if(king != null) {
            assert king != player : "The king wanted to become the king again...";
            king.setKing(false);
        }

        // set new king
        player.setKing(true);
        king = player;
        broadcast("event.king.new", king.getName());

        // Add score
        king.addScore(getRules().getScoreKingBonus());
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
        // 3) Start score timer
        scoreTimer = ReignOfCubes2.runTaskTimer(() -> {
            if(hasKing()) {
                king.addScore(getRules().getScoreKingPerSecond());
            }
        }, 1);

        //TODO message
        Bukkit.broadcastMessage("§2§l > game started.");
    }

    public void stop() {
        if(!isPlaying()) {
            ReignOfCubes2.warning("Tried to stop the game... Was already the case.");
            return;
        }

        scoreTimer.cancel();
        scoreTimer = null;

        Bukkit.broadcastMessage("§4§l > game stopped.");
        if(king != null) {
            king.setKing(false);
            king = null;
        }

        state = GameState.WAITING;
        //TODO message, TP players, ...
    }

    public void broadcast(String entry, Object... args) {
        players.broadcast(entry, args);
    }

    public int getPlayersCount() {
        return players.size();
    }

    public @Nullable Ceremony getCeremony() {
        if(throne == null || ! throne.hasCeremony())
            return null;
        return throne.getCeremony();
    }

}
