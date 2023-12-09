package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import static fr.jamailun.reignofcubes2.configuration.WorldConfiguration.BadWorldConfigurationException;
import fr.jamailun.reignofcubes2.players.PlayersManager;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class GameManager {

    private final PlayersManager players;

    @Getter private GameState state = GameState.WAITING;
    @Getter private WorldConfiguration worldConfiguration;
    private World world;
    @Getter private RocPlayer king;
    @Getter private Throne throne;

    public GameManager(PlayersManager players) {
        this.players = players;
    }

    public void loadConfiguration(String configurationFile) throws BadWorldConfigurationException {
        WorldConfiguration wc = WorldConfiguration.load(configurationFile);
        if(wc == null) {
            throw new BadWorldConfigurationException("Configuration not found.");
        }
        if(!wc.isValid()) {
            throw new BadWorldConfigurationException("Configuration invalid.");
        }
        worldConfiguration = wc;
        throne = worldConfiguration.generateThrone();
        world = Bukkit.getWorld(worldConfiguration.getWorldName());
    }


    public void playerJoinsServer(Player p) {
        RocPlayer player = players.join(p);
        players.broadcast("event.joined", p.getName());
        player.sendMessage("event.joined.direct");

        // Has enough players
    }

    public void playerLeftServer(Player p) {
        if(!players.exists(p)) {
            return;
        }
        RocPlayer player = players.get(p);
        if(player.isKing()) {
            // not anymore
            players.broadcast("event.left.king", p.getName());
            player.setKing(false);
            setKing(null);
        } else {
            players.broadcast("event.left", p.getName());
        }
    }

    public void playerDies(Player v, Player k) {
        RocPlayer victim = players.get(v);

        if(k == null) {
            players.broadcast("event.death.alone", v.getName());
            //players.deathPenality();
            return;
        }
        players.broadcast("event.death", v.getName(), k.getName());


    }

    private void setKing(RocPlayer player) {
        if(player == null) {
            if(king != null) {
                players.broadcast("event.king.death", king.getName());
            }
            return;
        }
        player.setKing(true);
        king = player;
        players.broadcast("event.king.new", king.getName());
    }

    public boolean isInWorld(World w) {
        return this.world.equals(w);
    }

}
