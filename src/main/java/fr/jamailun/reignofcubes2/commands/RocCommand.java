package fr.jamailun.reignofcubes2.commands;

import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.ConfigurationsList;
import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RocCommand implements CommandExecutor, TabCompleter {

    private final static List<String> args_0 = List.of("config", "start", "stop", "help");

    private final static List<String> args_1_config = List.of("enable", "set-default", "list", "create", "delete", "edit");

    protected final ReignOfCubes2 plugin;
    public RocCommand(ReignOfCubes2 plugin) {
        this.plugin = plugin;
        PluginCommand cmd = Bukkit.getPluginCommand("roc");
        assert cmd != null;
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);

        ReignOfCubes2.info("Command 'roc' enabled.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            return sendHelp(sender);
        }
        String arg = args[0];
        args = next(args);

        if(arg.equalsIgnoreCase("help")) {
            return sendHelp(sender);
        }

        if(arg.equalsIgnoreCase("config")) {
            if(args.length == 0) return missingArgument(sender, args_1_config);
            arg = args[0];
            args = next(args);

            // Set the current configuration
            if(arg.equalsIgnoreCase("enable")) {
                if(args.length == 0) return error(sender, "Specify the configuration to use.");
                if(game().isPlaying()) {
                    return error(sender, "Cannot change configuration while playing.");
                }
                String name = args[0];
                if(!configs().contains(name))
                    return error(sender, "Unknown configuration: " + name);
                WorldConfiguration config = configs().get(name);
                if(!config.isValid())
                    return error(sender, "Configuration " + name + " is not valid.");
                info(sender, "Enabling configuration " + config);
                if(game().loadConfiguration(config)) {
                    //TODO message ?
                    return info(sender, "Configuration has been enabled successful.");
                }
                return error(sender, "Configuration change failed. Check the logs.");
            }

            if(arg.equalsIgnoreCase("set-default")) {

            }

            // list of configurations names
            if(arg.equalsIgnoreCase("list")) {
                if(configs().size() == 0) {
                    return info(sender, "No configuration exist.");
                }
                info(sender, "§7Configurations list (" + configs().size() + "):");
                configs().list().forEach(n ->
                        info(sender, "§6- " + n.getName() + "§7 by " + n.getAuthor() + " on " + n.getWorldName()
                                + " : " + (n.isValid() ? "§a[VALID]" : "§c[INVALID]")
                ));
                return true;
            }

            // Create a new configuration
            if(arg.equalsIgnoreCase("create")) {
                if(args.length < 2) return error(sender, "Specify the name and the world of the configuration.");
                String name = args[0];
                String author = sender.getName();
                World world = Bukkit.getWorld(args[1]);
                if(world == null)
                    return error(sender, "The world '" + args[1] + "' is not valid.");
                if(configs().contains(name))
                    return error(sender, "The configuration name '" + name + "' already exists.");
                if(configs().createNewConfiguration(name, author, world) == null) {
                    return error(sender, "An error occurred when creating the configuration. Check the logs.");
                }
                return info(sender, "Configuration " + name + " successfully created. Edit it to use it.");
            }

            if(arg.equalsIgnoreCase("delete")) {
                return error(sender, "TODO");
            }

            if(arg.equalsIgnoreCase("edit")) {
                return error(sender, "TODO");
            }

            return unexpectedArgument(sender, arg, args_1_config);
        }

        return unexpectedArgument(sender, arg, args_0);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            return args_0.stream().filter(a -> a.startsWith(args[0].toLowerCase())).toList();
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("config")) {
                return args_1_config.stream().filter(a -> a.startsWith(args[1].toLowerCase())).toList();
            }
        }
        return Collections.emptyList();
    }

    private boolean sendHelp(CommandSender sender) {
        sender.sendMessage("§a TODO help menu");
        return true;
    }

    private boolean unexpectedArgument(CommandSender sender, String arg, List<String> allowed) {
        sender.sendMessage("§c invalid arg '" + arg + "'. Expected : " + Arrays.toString(allowed.toArray()));

        return true;
    }

    private boolean missingArgument(CommandSender sender, List<String> allowed) {
        sender.sendMessage("§c argument missing. Expected : " + Arrays.toString(allowed.toArray()));

        return true;
    }

    private boolean error(CommandSender sender, String message) {
        sender.sendMessage("§c" + message);
        return true;
    }

    private boolean info(CommandSender sender, String message) {
        sender.sendMessage("§a" + message);
        return true;
    }

    private @NotNull String[] next(@NotNull String[] source) {
        if(source.length == 0) return new String[0];
        String[] target = new String[source.length - 1];
        System.arraycopy(source, 1, target, 0, target.length);
        return target;
    }

    protected GameManager game() {
        return plugin.getGameManager();
    }

    protected ConfigurationsList configs() {
        return game().getConfigurationsList();
    }
}
