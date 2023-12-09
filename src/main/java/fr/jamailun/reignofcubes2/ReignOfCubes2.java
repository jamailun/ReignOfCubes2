package fr.jamailun.reignofcubes2;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class ReignOfCubes2 extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "salut");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
