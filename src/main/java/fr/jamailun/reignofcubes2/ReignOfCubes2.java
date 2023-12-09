package fr.jamailun.reignofcubes2;

import fr.jamailun.reignofcubes2.players.PlayersManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ReignOfCubes2 extends JavaPlugin {

    private PlayersManager playersManager = new PlayersManager();
    @Getter private GameManager gameManager;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Enabling plugin.");

        gameManager = new GameManager(playersManager);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Disabling plugin.");
    }

}
