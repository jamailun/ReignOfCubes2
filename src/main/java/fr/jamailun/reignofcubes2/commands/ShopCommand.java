package fr.jamailun.reignofcubes2.commands;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.gui.ShopGUI;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ShopCommand extends AbstractCommand {

    public ShopCommand(ReignOfCubes2 plugin) {
        super(plugin, "shop");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            return error(sender, "Must be a player tp open shop.");
        }

        if(!game().isPlaying()) {
            return error(sender, "Can only open the shop while playing.");
        }

        RocPlayer player = game().toPlayer((Player) sender);
        if(player == null)
            return error(sender, "Tu n'es pas dans le jeu. Déso pas déso.");

        new ShopGUI(player);

        return info(sender, "Shop opened.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean sendHelp(CommandSender sender) {
        return false;
    }
}
