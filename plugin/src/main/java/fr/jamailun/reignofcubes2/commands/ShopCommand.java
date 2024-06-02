package fr.jamailun.reignofcubes2.commands;

import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.api.ReignOfCubes2;
import fr.jamailun.reignofcubes2.api.players.RocPlayer;
import fr.jamailun.reignofcubes2.gui.AdminKitsGUI;
import fr.jamailun.reignofcubes2.gui.ShopGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ShopCommand extends AbstractCommand {

    public ShopCommand(MainROC2 plugin) {
        super(plugin, "shop");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player pl)) {
            return error(sender, "Must be a player tp open shop.");
        }

        RocPlayer player = ReignOfCubes2.findPlayer(pl);
        if(player == null)
            return error(sender, "Tu n'es pas dans le jeu. Déso pas déso.");

        if(!game().isStatePlaying()) {
            info(sender, "La partie n'ayant pas encore commencé, voici les kits actuellement configurés.");
            new AdminKitsGUI(player);
            return true;
        }

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
