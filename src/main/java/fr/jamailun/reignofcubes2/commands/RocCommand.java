package fr.jamailun.reignofcubes2.commands;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RocCommand implements CommandExecutor, TabCompleter {

    protected final ReignOfCubes2 plugin;
    public RocCommand(ReignOfCubes2 plugin) {
        this.plugin = plugin;
        PluginCommand cmd = Bukkit.getPluginCommand("roc");
        assert cmd != null;
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);

        Bukkit.getLogger().info("Command 'roc' enabled.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
