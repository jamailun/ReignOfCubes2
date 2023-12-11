package fr.jamailun.reignofcubes2.commands;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import fr.jamailun.reignofcubes2.GameManager;
import fr.jamailun.reignofcubes2.GameState;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.ConfigurationsList;
import fr.jamailun.reignofcubes2.configuration.GameRules;
import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import fr.jamailun.reignofcubes2.messages.Messages;
import fr.jamailun.reignofcubes2.utils.WorldEditHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RocCommand implements CommandExecutor, TabCompleter {

    private final static Vector MODIFIER_A = new Vector(0, 0, 0);
    private final static Vector MODIFIER_B = new Vector(1, 1, 1);

    private final static List<String> args_0 = List.of("config", "start", "stop", "help", "reload", "show");
    private final static List<String> args_1_reload = List.of("messages");
    private final static List<String> args_1_config = List.of("enable", "set-default", "list", "create", "delete", "edit", "edit.spawns", "show");
    private final static List<String> args_list = List.of("add", "remove", "list");

    private final static List<String> args_2_edit = List.of(
            "players.min", "players.max",
            "throne.pos_a", "throne.pos_b", "throne.pos",
            "crowning-duration", "crowning-duration.steal",
            "spawn.safe-distance",
            "scoring.goal", "scoring.king.bonus", "scoring.king.per-second",
            "scoring.kill.flat", "scoring.kill.steal", "scoring.death-penalty"
    );

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
                info(sender, "Enabling configuration " + config + "...");
                if(game().loadConfiguration(config)) {
                    //TODO message ?
                    return info(sender, "Configuration has been enabled successful.");
                }
                return error(sender, "Configuration change failed. Check the logs.");
            }

            if(arg.equalsIgnoreCase("set-default")) {
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
                info(sender, "Setting configuration " + config + " as default.");
                configs().setDefault(config);
                return true;
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
                WorldConfiguration defaultConfig = configs().getDefault();
                if(defaultConfig != null) {
                    info(sender, "§7Default configuration : §6" + defaultConfig.getName());
                } else {
                    info(sender, "§7No default configuration !");
                }
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
                //TODO delete a configuration
                return error(sender, "TODO");
            }

            // Display the configuration
            if(arg.equalsIgnoreCase("show")) {
                String configName = args[0];
                if(!configs().contains(configName))
                    return error(sender, "Unknown configuration: " + configName);
                WorldConfiguration config = configs().get(configName);
                return info(sender, "§rConfiguration: " + config.nicePrint());
            }

            // Edit a configuration
            if(arg.equalsIgnoreCase("edit")) {
                if(args.length < 2) return error(sender, "Specify the config-name and the property to edit.");
                String configName = args[0];
                String property = args[1].toLowerCase();
                if(!configs().contains(configName))
                    return error(sender, "Unknown configuration: " + configName);
                WorldConfiguration config = configs().get(configName);

                if(property.equals("throne.pos_a") || property.equals("throne.pos_b") || property.equals("throne.pos")) {
                    if(!(sender instanceof Entity)) {
                        return error(sender, "You must be a player to change the throne position.");
                    }
                    Vector vector = ((Player)sender).getLocation().toVector();
                    if(property.equals("throne.pos_a")) {
                        vector = new Vector(
                                Math.floor(vector.getX()),
                                Math.floor(vector.getY()),
                                Math.floor(vector.getZ())
                        );
                        config.setThroneA(vector.add(MODIFIER_A));
                        info(sender, "Position of throne has been updated to "+niceVector(vector)+" for §6" + configName);
                    } else if(property.equals("throne.pos_b")) {
                        vector = new Vector(
                                Math.floor(vector.getX()),
                                Math.ceil(vector.getY()),
                                Math.floor(vector.getZ())
                        );
                        config.setThroneB(vector.add(MODIFIER_B));
                        info(sender, "Position of throne has been updated to "+niceVector(vector)+" for §6" + configName);
                    } else {
                        Region region = WorldEditHandler.getSelectedRegion(sender.getName());
                        if(region == null) return error(sender, "Select a zone with WE to use this.");
                        Vector3 min = region.getMinimumPoint().toVector3();
                        Vector3 max = region.getMaximumPoint().toVector3();
                        config.setThroneA(new Vector(min.getX(), min.getY(), min.getZ()).add(MODIFIER_A));
                        config.setThroneB(new Vector(max.getX(), max.getY(), max.getZ()).add(MODIFIER_B));
                        info(sender, "Position of throne has been updated for §6" + configName
                                + "§a to §e" + min + "§a ; §e" + max);
                    }
                    return saveConfiguration(sender, config);
                }
                if(args.length < 3)
                    return error(sender, "To change this property, specify a value");
                String value = args[2];

                GameRules rules = config.getRules();
                String success = "Configuration §6"+configName+"§a has been updated successfully.";
                boolean isSuccess = switch(property) {
                    case "players.min" -> setInt(sender, value, rules::setPlayerCountMin, success);
                    case "players.max" -> setInt(sender, value, rules::setPlayerCountMax, success);
                    case "crowning-duration" -> setDouble(sender, value, rules::setCrownDuration, success);
                    case "crowning-duration.steal" -> setDouble(sender, value, rules::setCrownDurationSteal, success);
                    case "spawn.safe-distance" -> setDouble(sender, value, rules::setSpawnSafeDistance, success);
                    case "scoring.goal" -> setInt(sender, value, rules::setScoreGoal, success);
                    case "scoring.king.bonus" -> setInt(sender, value, rules::setScoreKingBonus, success);
                    case "scoring.king.per-second" -> setInt(sender, value, rules::setScoreKingPerSecond, success);
                    case "scoring.kill.flat" -> setInt(sender, value, rules::setScoreKillFlat, success);
                    case "scoring.kill.steal" -> setDouble(sender, value, rules::setScoreKillSteal, success);
                    case "scoring.death-penalty" -> setInt(sender, value, rules::setScoreDeathPenalty, success);
                    default -> unexpectedArgument(sender, property, args_2_edit);
                };
                if(isSuccess) {
                    return saveConfiguration(sender, config);
                }
                return true;
            }

            if(arg.equalsIgnoreCase("edit.spawns")) {
                if(args.length < 2) return error(sender, "Specify the config and the mode.");
                String configName = args[0];
                arg = args[1].toLowerCase();
                if(!configs().contains(configName))
                    return error(sender, "Unknown configuration: " + configName);
                WorldConfiguration config = configs().get(configName);

                if(arg.equals("list")) {
                    if(config.spawnsList().isEmpty()) {
                        return info(sender, "§7No spawn-point set for §6"+configName+"§7.");
                    }
                    info(sender, "§7Spawn-points ("+config.spawnsList().size()+") :");
                    config.spawnsList().forEach(s -> info(sender, "§7- " + niceVector(s)));
                    return true;
                }

                if(arg.equals("add")) {
                    if(!(sender instanceof Entity)) {
                        return error(sender, "Must be an entity to add a point.");
                    }
                    config.spawnsList().add(((Entity)sender).getLocation().toVector());
                    info(sender, "Added a spawn-point to the configuration.");
                    return saveConfiguration(sender, config);
                }

                if(arg.equals("remove")) {
                    if(args.length < 3)
                        return error(sender, "Specify the index of the spawn to remove.");
                    int index;
                    try {
                        index = Integer.parseInt(args[2]);
                    } catch(NumberFormatException e) {
                        return error(sender, "Invalid integer format: " + args[2]);
                    }
                    if(index < 0 || index >= config.spawnsList().size()) {
                        return error(sender, "Invalid index. Should be positive and under " + config.spawnsList().size());
                    }
                    config.spawnsList().remove(index);
                    info(sender, "Removed a spawn-point to the configuration.");
                    return saveConfiguration(sender, config);
                }

                return unexpectedArgument(sender, arg, args_list);
            }

            return unexpectedArgument(sender, arg, args_1_config);
        }

        if(arg.equalsIgnoreCase("start")) {
            if(game().getState() == GameState.NOT_CONFIGURED) {
                return error(sender, "The game state must be configured first.");
            }
            if(game().isPlaying()) {
                return error(sender, "The game already started.");
            }
            game().start();
            return info(sender, "Command executed.");
        }

        if(arg.equalsIgnoreCase("stop")) {
            if(!game().isPlaying()) {
                return error(sender, "Cannot stop a non-stopped game.");
            }
            game().stop();
            return info(sender, "Command executed.");
        }

        if(arg.equalsIgnoreCase("reload")) {
            if(args.length < 1) return missingArgument(sender, args_1_reload);
            arg = args[0];

            if(arg.equalsIgnoreCase("messages")) {
                Messages.reload();
                return info(sender, "Messages configuration reloaded.");
            }

            return unexpectedArgument(sender, arg, args_1_reload);
        }

        if(arg.equalsIgnoreCase("show")) {
            if(!(sender instanceof Player)) return error(sender, "Must be a player.");
            if(args.length < 1) return error(sender, "Specify the config to show.");

            String name = args[0];
            if(!configs().contains(name))
                return error(sender, "Unknown configuration: " + name);
            WorldConfiguration config = configs().get(name);
            boolean result = config.debug.toggle((Player)sender);
            return info(sender, "Debug has been toggled (" + result + ").");
        }


        return unexpectedArgument(sender, arg, args_0);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            return args_0.stream().filter(a -> a.startsWith(args[0].toLowerCase())).toList();
        }
        else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("config")) {
                return args_1_config.stream().filter(a -> a.startsWith(args[1].toLowerCase())).toList();
            }
            else if(args[0].equalsIgnoreCase("reload")) {
                return args_1_reload.stream().filter(a -> a.startsWith(args[1].toLowerCase())).toList();
            }
            else if(args[0].equalsIgnoreCase("show")) {
                return configurationsNames().filter(a -> a.startsWith(args[1].toLowerCase())).toList();
            }
        }
        else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("config")) {
                if(     args[1].equalsIgnoreCase("edit")
                        || args[1].equalsIgnoreCase("edit.spawns")
                        || args[1].equalsIgnoreCase("set-default")
                        || args[1].equalsIgnoreCase("show")
                        || args[0].equalsIgnoreCase("enable")
                ) {
                    return configurationsNames().filter(a -> a.startsWith(args[2].toLowerCase())).toList();
                }
            }
        }
        else if(args.length == 4) {
            if(args[0].equalsIgnoreCase("config")) {
                if(args[1].equalsIgnoreCase("edit")) {
                    return args_2_edit.stream().filter(a -> a.startsWith(args[3].toLowerCase())).toList();
                }
                else if(args[1].equalsIgnoreCase("edit.spawns")) {
                    return args_list.stream().filter(a -> a.startsWith(args[3].toLowerCase())).toList();
                }
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

    private boolean setInt(CommandSender sender, String value, Consumer<Integer> consumer, String success) {
        try {
            int v = Integer.parseInt(value);
            consumer.accept(v);
            return info(sender, success);
        } catch(NumberFormatException ignored) {
            error(sender, "Invalid integer value: '"+value+"'");
            return false;
        }
    }
    private boolean setDouble(CommandSender sender, String value, Consumer<Double> consumer, String success) {
        try {
            double v = Double.parseDouble(value);
            consumer.accept(v);
            return info(sender, success);
        } catch(NumberFormatException ignored) {
            error(sender, "Invalid integer value: '"+value+"'");
            return false;
        }
    }

    private Stream<String> configurationsNames() {
        return configs().list().stream().map(WorldConfiguration::getName);
    }

    private String niceVector(Vector vector) {
        return "(" + vector.getX() + "," + vector.getY() + "," + vector.getZ() + ")";
    }

    protected boolean saveConfiguration(CommandSender sender, WorldConfiguration config) {
        try{
            config.save();
            return true;
        } catch(IOException e) {
            ReignOfCubes2.error("Could not save " + config.getName() + ": " + e.getMessage());
            return error(sender,"Could not save " + config.getName() + ": " + e.getMessage());
        }
    }

}
