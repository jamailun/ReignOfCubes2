package fr.jamailun.reignofcubes2.commands;

import fr.jamailun.reignofcubes2.GameManagerImpl;
import fr.jamailun.reignofcubes2.MainROC2;
import fr.jamailun.reignofcubes2.configuration.ConfigurationsList;
import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import fr.jamailun.reignofcubes2.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.players.RocPlayerImpl;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    protected final MainROC2 plugin;

    protected AbstractCommand(MainROC2 plugin, String command) {
        this.plugin = plugin;
        PluginCommand cmd = Bukkit.getPluginCommand(command);
        assert cmd != null;
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);

        MainROC2.info("Command '"+command+"' enabled.");
    }

    @Override
    public abstract boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args);

    @Override
    public abstract @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args);


    protected boolean unexpectedArgument(CommandSender sender, String arg, List<String> allowed) {
        return error(sender, "§c invalid arg '" + arg + "'. Expected : " + Arrays.toString(allowed.toArray()));
    }

    protected boolean missingArgument(CommandSender sender, List<String> allowed) {
        return error(sender, "§c argument missing. Expected : " + Arrays.toString(allowed.toArray()));
    }

    protected boolean error(CommandSender sender, String message) {
        sender.sendMessage("§4[§cERROR§4]§c " + message);
        return true;
    }

    protected boolean info(CommandSender sender, String message) {
        sender.sendMessage("§3[§fINFO§3]§7 " + message);
        return true;
    }

    protected boolean success(CommandSender sender, String message) {
        sender.sendMessage("§2[§aSUCCESS§2]§f " + message);
        return true;
    }

    protected @NotNull String[] next(@NotNull String[] source) {
        if(source.length == 0) return new String[0];
        String[] target = new String[source.length - 1];
        System.arraycopy(source, 1, target, 0, target.length);
        return target;
    }

    protected GameManagerImpl game() {
        return plugin.getGameManager();
    }

    protected ConfigurationsList configs() {
        return game().getConfigurationsList();
    }

    protected boolean setInt(CommandSender sender, String value, Consumer<Integer> consumer, String success) {
        try {
            int v = Integer.parseInt(value);
            consumer.accept(v);
            return info(sender, success);
        } catch(NumberFormatException ignored) {
            error(sender, "Invalid integer value: '"+value+"'");
            return false;
        }
    }
    protected boolean setDouble(CommandSender sender, String value, Consumer<Double> consumer, String success) {
        try {
            double v = Double.parseDouble(value);
            consumer.accept(v);
            return info(sender, success);
        } catch(NumberFormatException ignored) {
            error(sender, "Invalid integer value: '"+value+"'");
            return false;
        }
    }


    protected RocPlayerImpl getPlayer(CommandSender sender, String playerName) {
        Optional<RocPlayerImpl> player = game().findPlayer(playerName);
        if(player.isEmpty()) {
            error(sender, "Invalid player-name: '" + playerName + "'.");
            return null;
        }
        return player.get();
    }

    protected String absorbRemaining(int offset, String[] args) {
        StringJoiner sj = new StringJoiner(" ");
        for(int i = offset; i < args.length; i++)
            sj.add(args[i]);
        return sj.toString();
    }


    protected Stream<String> configurationsNames() {
        return configs().list().stream().map(WorldConfiguration::getName);
    }

    protected Stream<String> playersNames() {
        return game().players().map(RocPlayerImpl::getName);
    }

    protected Stream<String> kitsIds() {
        return MainROC2.getKits().getKits().stream().map(Kit::getId);
    }

    protected abstract boolean sendHelp(CommandSender sender);

}
