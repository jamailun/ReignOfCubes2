package fr.jamailun.reignofcubes2.commands;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import fr.jamailun.reignofcubes2.GameState;
import fr.jamailun.reignofcubes2.ReignOfCubes2;
import fr.jamailun.reignofcubes2.configuration.GameRules;
import fr.jamailun.reignofcubes2.configuration.TagsConfiguration;
import fr.jamailun.reignofcubes2.configuration.WorldConfiguration;
import fr.jamailun.reignofcubes2.configuration.kits.Kit;
import fr.jamailun.reignofcubes2.configuration.pickups.PickupConfigEntry;
import fr.jamailun.reignofcubes2.gui.AdminKitsGUI;
import fr.jamailun.reignofcubes2.messages.Messages;
import fr.jamailun.reignofcubes2.players.RocPlayer;
import fr.jamailun.reignofcubes2.tags.TagsRegistry;
import fr.jamailun.reignofcubes2.utils.WorldEditHandler;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * I'll clean this mess up. One day.
 */
public class RocCommand extends AbstractCommand {

    private final static Vector MODIFIER_A = new Vector(0, 0, 0);
    private final static Vector MODIFIER_B = new Vector(1, 1, 1);

    private final static List<String> args_0 = List.of("config", "start", "stop", "help", "reload", "show", "cheat", "kits", "tp.lobby");
    private final static List<String> args_1_start = List.of("game", "countdown");
    private final static List<String> args_1_reload = List.of("messages", "kits", "musics");
    private final static List<String> args_1_kits = List.of("gui", "from-inventory.new", "from-inventory.update", "give", "delete", "edit");
    private final static List<String> args_1_config = List.of("enable", "set-default", "list", "create",
            "delete", "edit", "edit.spawns", "edit.generators", "edit.pickups", "edit.tags",
            "show");
    private final static List<String> args_1_cheat = List.of("set.king", "set.score");
    private final static List<String> args_list = List.of("add", "remove", "list");
    private final static List<String> args_2_kits_edit = List.of("cost", "icon.type", "name", "tag");
    private final static List<String> args_3_get_set = List.of("get", "set");

    private final static List<String> args_2_edit = List.of(
            "players.min", "players.max",
            "throne.pos_a", "throne.pos_b", "throne.pos", "throne.cooldown",
            "lobby",
            "crowning-duration", "crowning-duration.steal",
            "spawn.safe-distance", "generator-frequency", "kill-y",
            "scoring.goal", "scoring.king.bonus", "scoring.king.per-second",
            "scoring.kill.flat", "scoring.kill.steal", "scoring.death-penalty",
            "shop-item"
    );
    private final static List<String> args_2_edit_tags = List.of(
            "regicide.attack.king.flat", "regicide.attack.king.mult",
            "regicide.attack.others.flat", "regicide.attack.others.mult",
            "regicide.defend.king.flat", "regicide.defend.king.mult",
            "regicide.defend.others.flat", "regicide.defend.others.mult"
    );

    public RocCommand(ReignOfCubes2 plugin) {
        super(plugin, "roc");
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
                    return success(sender, "Configuration has been enabled successful.");
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
                configs().setDefault(config);
                success(sender, "Set configuration " + config + " as default.");
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

            // Delete a configuration
            if(arg.equalsIgnoreCase("delete")) {
                //TODO delete a configuration
                return error(sender, "TODO");
            }

            // Display the configuration
            if(arg.equalsIgnoreCase("show")) {
                if(args.length < 1) {
                    return error(sender, "Specify the configuration to show.");
                }
                String configName = args[0];
                if(!configs().contains(configName))
                    return error(sender, "Unknown configuration: " + configName);
                WorldConfiguration config = configs().get(configName);
                return info(sender, "§rConfiguration: " + config.nicePrint());
            }


            if(arg.equalsIgnoreCase("edit.tags")) {
                if(args.length < 2) return error(sender, "Specify the config-name and the property to edit.");
                String configName = args[0];
                String property = args[1].toLowerCase();
                if(!configs().contains(configName))
                    return error(sender, "Unknown configuration: " + configName);
                WorldConfiguration config = configs().get(configName);
                if(args.length < 3)
                    return error(sender, "To change this tag-property, specify a value");
                String value = args[2];

                TagsConfiguration tags = config.getTagsConfiguration();
                String success = "TagsConfiguration §6"+configName+"§a has been updated successfully.";
                boolean isSuccess = switch(property) {
                    case "regicide.attack.king.flat" -> setDouble(sender, value, tags::setRegicideAttackFlatKing, success);
                    case "regicide.attack.king.mult" -> setDouble(sender, value, tags::setRegicideAttackMultiplicativeKing, success);
                    case "regicide.attack.others.flat" -> setDouble(sender, value, tags::setRegicideAttackFlatOthers, success);
                    case "regicide.attack.others.mult" -> setDouble(sender, value, tags::setRegicideAttackMultiplicativeOthers, success);
                    case "regicide.defend.king.flat" -> setDouble(sender, value, tags::setRegicideDefendFlatKing, success);
                    case "regicide.defend.king.mult" -> setDouble(sender, value, tags::setRegicideDefendMultiplicativeKing, success);
                    case "regicide.defend.others.flat" -> setDouble(sender, value, tags::setRegicideDefendFlatOthers, success);
                    case "regicide.defend.others.mult" -> setDouble(sender, value, tags::setRegicideDefendMultiplicativeOthers, success);
                    default -> unexpectedArgument(sender, property, args_2_edit);
                };
                if(isSuccess) {
                    return saveConfiguration(sender, config);
                }
                return true;
            }

            // Edit a configuration
            if(arg.equalsIgnoreCase("edit")) {
                if(args.length < 2) return error(sender, "Specify the config-name and the property to edit.");
                String configName = args[0];
                String property = args[1].toLowerCase();
                if(!configs().contains(configName))
                    return error(sender, "Unknown configuration: " + configName);
                WorldConfiguration config = configs().get(configName);

                if(     property.equals("throne.pos_a")
                        || property.equals("throne.pos_b")
                        || property.equals("throne.pos")
                        || property.equals("lobby")
                ) {
                    if(!(sender instanceof Entity)) {
                        return error(sender, "You must be a player to change the throne position.");
                    }
                    Vector vector = ((Player)sender).getLocation().toVector();
                    switch (property) {
                        case "lobby" -> {
                            config.setLobby(((Player)sender).getLocation());
                            info(sender, "Position of lobby has been updated to " + niceVector(vector) + " for §6" + configName);
                        }
                        case "throne.pos_a" -> {
                            vector = new Vector(
                                    Math.floor(vector.getX()),
                                    Math.floor(vector.getY()),
                                    Math.floor(vector.getZ())
                            );
                            config.setThroneA(vector.add(MODIFIER_A));
                            info(sender, "Position of throne has been updated to " + niceVector(vector) + " for §6" + configName);
                        }
                        case "throne.pos_b" -> {
                            vector = new Vector(
                                    Math.floor(vector.getX()),
                                    Math.ceil(vector.getY()),
                                    Math.floor(vector.getZ())
                            );
                            config.setThroneB(vector.add(MODIFIER_B));
                            info(sender, "Position of throne has been updated to " + niceVector(vector) + " for §6" + configName);
                        }
                        default -> {
                            Region region = WorldEditHandler.getSelectedRegion(sender.getName());
                            if (region == null) return error(sender, "Select a zone with WE to use this.");
                            Vector3 min = region.getMinimumPoint().toVector3();
                            Vector3 max = region.getMaximumPoint().toVector3();
                            config.setThroneA(new Vector(min.getX(), min.getY(), min.getZ()).add(MODIFIER_A));
                            config.setThroneB(new Vector(max.getX(), max.getY(), max.getZ()).add(MODIFIER_B));
                            info(sender, "Position of throne has been updated for §6" + configName
                                    + "§a to §e" + min + "§a ; §e" + max);
                        }
                    }
                    return saveConfiguration(sender, config);
                }

                if(args.length < 3)
                    return error(sender, "To change this property, specify a value");
                String value = args[2];

                if(property.equals("shop-item")) {
                    if(! (sender instanceof Player p)) return error(sender, "Must be a player.");
                    if(value.equals("get")) {
                        ItemStack item = config.getShopItem();
                        if(item == null) return error(sender, "No current item.");
                        p.getInventory().addItem(item);
                        return info(sender, "Current item added.");
                    } else if(value.equals("set")) {
                        ItemStack item = p.getInventory().getItemInMainHand();
                        if(item.getType().isAir()) return error(sender, "Hold an item in hand.");
                        config.setShopItem(item);
                        if(saveConfiguration(sender, config)) {
                            success(sender, "ShopItem changed successfully.");
                        }
                        return true;
                    }
                    return unexpectedArgument(sender, value, args_3_get_set);
                }

                GameRules rules = config.getRules();
                String success = "Configuration §6"+configName+"§a has been updated successfully.";
                boolean isSuccess = switch(property) {
                    case "players.min" -> setInt(sender, value, rules::setPlayerCountMin, success);
                    case "players.max" -> setInt(sender, value, rules::setPlayerCountMax, success);
                    case "crowning-duration" -> setDouble(sender, value, rules::setCrownDuration, success);
                    case "crowning-duration.steal" -> setDouble(sender, value, rules::setCrownDurationSteal, success);
                    case "generator-frequency" -> setDouble(sender, value, rules::setGeneratorFrequency, success);
                    case "kill-y" -> setDouble(sender, value, rules::setKillY, success);
                    case "spawn.safe-distance" -> setDouble(sender, value, rules::setSpawnSafeDistance, success);
                    case "throne.cooldown" -> setDouble(sender, value, rules::setThroneCooldown, success);
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

            // Edit the spawns of a configuration
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

            // Edit generators
            if(arg.equalsIgnoreCase("edit.generators")) {
                if(args.length < 2) return error(sender, "Specify the config and the mode.");
                String configName = args[0];
                arg = args[1].toLowerCase();
                if(!configs().contains(configName))
                    return error(sender, "Unknown configuration: " + configName);
                WorldConfiguration config = configs().get(configName);

                if(arg.equals("list")) {
                    if(config.listGenerators().isEmpty()) {
                        return info(sender, "§7No generators set for §6"+configName+"§7.");
                    }
                    info(sender, "§7generators ("+config.listGenerators().size()+") :");
                    config.listGenerators().forEach(s -> info(sender, "§7- " + niceVector(s)));
                    return true;
                }

                if(arg.equals("add")) {
                    if(!(sender instanceof Entity)) {
                        return error(sender, "Must be an entity to add a generator.");
                    }
                    config.listGenerators().add(((Entity)sender).getLocation().toVector());
                    info(sender, "Added a generator to the configuration.");
                    return saveConfiguration(sender, config);
                }

                if(arg.equals("remove")) {
                    if(args.length < 3)
                        return error(sender, "Specify the index of the generator to remove.");
                    int index;
                    try {
                        index = Integer.parseInt(args[2]);
                    } catch(NumberFormatException e) {
                        return error(sender, "Invalid integer format: " + args[2]);
                    }
                    if(index < 0 || index >= config.listGenerators().size()) {
                        return error(sender, "Invalid index. Should be positive and under " + config.listGenerators().size());
                    }
                    config.listGenerators().remove(index);
                    info(sender, "Removed a generator from the configuration.");
                    return saveConfiguration(sender, config);
                }

                return unexpectedArgument(sender, arg, args_list);
            }

            //
            if(arg.equalsIgnoreCase("edit.pickups")) {
                if(args.length < 2) return error(sender, "Specify the config and the mode.");
                String configName = args[0];
                arg = args[1].toLowerCase();
                if(!configs().contains(configName))
                    return error(sender, "Unknown configuration: " + configName);
                WorldConfiguration config = configs().get(configName);

                if("list".equalsIgnoreCase(arg)) {
                    if(config.getPickupConfiguration().isEmpty()) {
                        return info(sender, "§7No pickup-entry set for §6"+configName+"§7.");
                    }
                    List<PickupConfigEntry> pickups = config.getPickupConfiguration().listEntries();
                    info(sender, "§7Pick-up entries ("+pickups.size()+") :");
                    pickups.forEach(p -> info(sender, "§7- §e" + p));
                    return true;
                }

                if("color".equalsIgnoreCase(arg)) {
                    if(!(sender instanceof Player))
                        return error(sender, "You must be a player to do that.");
                    if(args.length < 3)
                        return error(sender, "Specify the ID of the generator to remove.");
                    String id = args[2];
                    PickupConfigEntry entry = config.getPickupConfiguration().listEntries()
                            .stream()
                            .filter(e -> e.id().equals(id))
                            .findFirst()
                            .orElse(null);
                    if(entry == null) {
                        return error(sender, "Unknown pickup-entry: '" + id + "'.");
                    }
                    entry.spawnFirework(((Player)sender).getLocation());
                    return info(sender, "§oWhooooosh");
                }

                if(arg.equals("add")) {
                    if(args.length < 7) {// 0,1 ; 2=id 3=mat 4=chance 5=score 6=color
                        return error(sender, "Syntax : /"+label+" edit.pickups add §l<id> <material> <chance> <score> <color>");
                    }
                    String id = args[2];
                    Material material;
                    int score;
                    double chance;
                    Color color;
                    try {
                        material = Material.valueOf(args[3].toUpperCase());
                    } catch(Exception e) { return error(sender, "Invalid material '" + args[3] + "'."); }
                    try {
                        score = Integer.parseInt(args[4]);
                        if(score <= 0)
                            return error(sender, "Score must be (strictly) positive !");
                    } catch(Exception e) { return error(sender, "Invalid score (int) '" + args[4] + "'."); }
                    try {
                        chance = Double.parseDouble(args[5]);
                    } catch(Exception e) { return error(sender, "Invalid chance (double) '" + args[5] + "'."); }
                    try {
                        color = parseColor(args[6]);
                    } catch(Exception e) { return error(sender, "Invalid color (r,g,b) '" + args[5] + "' : " + e.getMessage()); }

                    PickupConfigEntry entry = new PickupConfigEntry(id, material, score, chance, color);
                    config.getPickupConfiguration().add(entry);
                    success(sender, "Added a pickup-entry to the configuration.");
                    return saveConfiguration(sender, config);
                }

                if(arg.equals("remove")) {
                    if(args.length < 3)
                        return error(sender, "Specify the ID of the generator to remove.");
                    String id = args[2];
                    config.getPickupConfiguration().remove(id);
                    info(sender, "Removed a pickup-entry from the configuration.");
                    return saveConfiguration(sender, config);
                }

                return unexpectedArgument(sender, arg, args_list);
            }

            return unexpectedArgument(sender, arg, args_1_config);
        }

        if(arg.equalsIgnoreCase("tp.lobby")) {
            if(game().isPlaying()) {
                return error(sender, "Cannot interrupt game while playing. Stop-it first.");
            }
            WorldConfiguration config = game().getWorldConfiguration();
            if(config == null || ! config.isValid())
                return error(sender, "Invalid configuration. Either on-set or invalid.");
            Location lobby = game().getWorldConfiguration().getLobby();
            game().players()
                    .filter(RocPlayer::isValid)
                    .map(RocPlayer::getPlayer)
                    .forEach(p -> {
                        info(p, "Téléporté au lobby par " + sender.getName());
                        p.teleport(lobby);
                    });
            return info(sender, "All players teleported to lobby.");
        }

        if(arg.equalsIgnoreCase("start")) {
            if(game().getState() == GameState.NOT_CONFIGURED)
                return error(sender, "The game state must be configured first.");
            if(args.length < 1) return missingArgument(sender, args_1_start);

            // Start game
            if(args[0].equalsIgnoreCase("game")) {
                if(game().getState() == GameState.PLAYING) {
                    return error(sender, "The game already started !");
                }
                info(sender, "Starting game.");
                game().broadcast("game.start-force", sender.getName());
                game().start();
                return true;
            }

            // Start countdown
            if(args[0].equalsIgnoreCase("countdown")) {
                if(game().getState() == GameState.COUNT_DOWN)
                    return error(sender, "The count-down already started !");
                if(game().getState() == GameState.PLAYING)
                    return error(sender, "The game already started !");
                info(sender, "Starting count-down.");
                game().broadcast("countdown.start-force", sender.getName());
                game().startCountdown();
                return true;
            }

            return unexpectedArgument(sender, args[0], args_1_start);
        }

        if(arg.equalsIgnoreCase("stop")) {
            if(game().isPlaying()) {
                info(sender, "Stopping game.");
                game().broadcast("game.cancelled", sender.getName());
                game().stop();
                return true;
            }
            if(game().isCountdown()) {
                info(sender, "Stopping countdown.");
                game().broadcast("countdown.cancelled-force", sender.getName());
                game().stopCountdown();
                return true;
            }
            return error(sender, "Cannot stop a game in state §4"+game().getState()+"§c.");
        }

        if(arg.equalsIgnoreCase("reload")) {
            if(args.length < 1) return missingArgument(sender, args_1_reload);
            arg = args[0];

            if(arg.equalsIgnoreCase("messages")) {
                Messages.reload();
                return success(sender, "Messages configuration reloaded.");
            }

            if(arg.equalsIgnoreCase("musics")) {
                game().getMusics().reload();
                return success(sender, "Musics reloaded.");
            }

            if(arg.equalsIgnoreCase("kits")) {
                ReignOfCubes2.getKits().reload();
                return success(sender, "Kits configuration reloaded.");
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
            return info(sender, "Debug showing has been toggled (" + result + ").");
        }

        if(arg.equalsIgnoreCase("cheat")) {
            if(!game().isPlaying()) return error(sender, "Cannot cheat before the game starts.");
            if(args.length < 1) return missingArgument(sender, args_1_cheat);
            arg = args[0];
            args = next(args);

            if(arg.equalsIgnoreCase("set.king")) {
                // clear
                if(args.length < 1) {
                    game().cheat.forceKing(null);
                    return info(sender, "King cleared");
                }
                RocPlayer target = getPlayer(sender, args[0]);
                if(target == null) return true;
                game().cheat.forceKing(target);
                return info(sender, "King set.");
            }

            if(arg.equalsIgnoreCase("set.score")) {
                if(args.length < 2)
                    return error(sender, "/" + label + " cheat set.score <player> <score>");
                RocPlayer target = getPlayer(sender, args[0]);
                if(target == null) return true;
                int score;
                try {
                    score = Integer.parseInt(args[1]);
                } catch(NumberFormatException ignored) {
                    return error(sender, "Invalid number: '" + args[1] + "'.");
                }
                game().cheat.forceScore(target, score);
                return info(sender, "Score of "+target.getName()+" set to "+score+".");
            }

            return unexpectedArgument(sender, args[0], args_1_cheat);
        }

        if(arg.equalsIgnoreCase("kits")) {
            if(!(sender instanceof Player)) return error(sender, "Cannot use 'kits' subcommand as a console.");
            RocPlayer player = game().toPlayer((Player) sender);
            if(player == null) return error(sender, "Tu n'es pas dans le jeu. déso");

            if(args.length < 1) return missingArgument(sender, args_1_kits);
            arg = args[0];
            args = next(args);
            if(!args_1_kits.contains(arg.toLowerCase())) return unexpectedArgument(sender, arg, args_1_kits);

            // GUI
            if(arg.equalsIgnoreCase("gui")) {
                new AdminKitsGUI(player);
                return info(sender, "GUI ouverte.");
            }

            //  "give", "delete", "edit"
            if(arg.equalsIgnoreCase("from-inventory.new")) {
                if(args.length < 2) return error(sender, "Argument missing. Syntax: \"/"+label+", kits " + arg + " §4<id> <display name>§c.\"");
                String id = args[0];
                String name = absorbRemaining(1, args);

                Kit kit = ReignOfCubes2.getKits().create(id, name);
                if(kit == null) {
                    return error(sender, "The id '§4"+id+"§c' already exists.");
                }

                kit.loadFromInventory(player);
                kit.save();

                return success(sender, "Kit created successfully. Edit it before using it.");
            }

            // Kit
            if(args.length < 1) return error(sender, "You must specify what kit to use for this command.");
            String kitId = args[0];
            Kit kit = ReignOfCubes2.getKits().getKit(kitId);
            if(kit == null) return error(sender, "Unknown kit id : '" + kitId + "'.");

            // == actions as a console
            if(arg.equalsIgnoreCase("edit")) {
                if("tag.remove".equalsIgnoreCase(args[1])) {
                    kit.setTagId(null);
                    return info(sender, "Removed tag from kit");
                }
                if(args.length < 3) return error(sender, "Specify value to change, and the new value.");
                switch(args[1].toLowerCase()) {
                    case "cost":
                        setInt(sender, args[2], kit::setCost, "Cost changed successfully.");
                        break;
                    case "icon.type":
                        try {
                            kit.setIconType(Material.valueOf(args[2].toUpperCase()));
                        } catch(IllegalArgumentException e) {
                            return error(sender, "Invalid material type: §4"+args[2]);
                        }
                        break;
                    case "name":
                        String newValue = absorbRemaining(2, args);
                        kit.setDisplayName(newValue);
                        break;
                    case "tag":
                        String tag = args[2];
                        kit.setTagId(tag);
                        if(TagsRegistry.find(tag) == null) {
                            kit.save();
                            return info(sender, "Kit saved, but tag §c" + tag + " §f is unknown.");
                        }
                        break;
                    default:
                        return unexpectedArgument(sender, args[2], args_2_kits_edit);
                }
                kit.save();
                return success(sender, "Kit saved successfully.");
            }

            if(arg.equalsIgnoreCase("delete")) {
                ReignOfCubes2.getKits().delete(kit);
                return success(sender, "Kit deleted.");
            }

            if(arg.equalsIgnoreCase("from-inventory.update")) {
                kit.loadFromInventory(player);
                kit.save();
                return success(sender, "Content of kit " + kitId + " updated.");
            }

            if(arg.equalsIgnoreCase("give")) {
                kit.equip(player);
                return success(sender, "Kit equipped.");
            }

            return error(sender, "Je sais pas coder. J'ai oublié '" + arg + "'.");
        }

        return unexpectedArgument(sender, arg, args_0);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            return args_0.stream().filter(a -> a.startsWith(args[0].toLowerCase())).toList();
        }
        else if(args.length == 2) {
            String arg1 = args[1].toLowerCase();
            if(args[0].equalsIgnoreCase("config")) {
                return args_1_config.stream().filter(a -> a.startsWith(arg1)).toList();
            }
            else if(args[0].equalsIgnoreCase("reload")) {
                return args_1_reload.stream().filter(a -> a.startsWith(arg1)).toList();
            }
            else if(args[0].equalsIgnoreCase("start")) {
                return args_1_start.stream().filter(a -> a.startsWith(arg1)).toList();
            }
            else if(args[0].equalsIgnoreCase("cheat")) {
                return args_1_cheat.stream().filter(a -> a.startsWith(arg1)).toList();
            }
            else if(args[0].equalsIgnoreCase("show")) {
                return configurationsNames().filter(a -> a.startsWith(arg1)).toList();
            }
            else if(args[0].equalsIgnoreCase("kits")) {
                return args_1_kits.stream().filter(a -> a.startsWith(arg1)).toList();
            }
        }
        else if(args.length == 3) {
            String arg2 = args[2].toLowerCase();
            if(args[0].equalsIgnoreCase("config")) {
                if(     args[1].equalsIgnoreCase("edit")
                        || args[1].equalsIgnoreCase("edit.spawns")
                        || args[1].equalsIgnoreCase("edit.generators")
                        || args[1].equalsIgnoreCase("edit.pickups")
                        || args[1].equalsIgnoreCase("edit.tags")
                        || args[1].equalsIgnoreCase("set-default")
                        || args[1].equalsIgnoreCase("show")
                        || args[1].equalsIgnoreCase("enable")
                ) {
                    return configurationsNames().filter(a -> a.startsWith(arg2)).toList();
                }
            }
            if(args[0].equalsIgnoreCase("kits")) {
                if(     args[1].equalsIgnoreCase("from-inventory.update")
                        || args[1].equalsIgnoreCase("give")
                        || args[1].equalsIgnoreCase("delete")
                        || args[1].equalsIgnoreCase("edit")
                ) {
                    return kitsIds().filter(k -> k.startsWith(arg2)).toList();
                }
            }
            if(args[0].equalsIgnoreCase("cheat")) {
                if(     args[1].equalsIgnoreCase("set.king")
                        || args[1].equalsIgnoreCase("set.score")
                ) {
                    return playersNames().filter(a -> a.startsWith(arg2)).toList();
                }
            }
        }
        else if(args.length == 4) {
            String arg3 = args[3].toLowerCase();
            if(args[0].equalsIgnoreCase("config")) {
                if(args[1].equalsIgnoreCase("edit")) {
                    return args_2_edit.stream().filter(a -> a.startsWith(arg3)).toList();
                }
                else if(args[1].equalsIgnoreCase("edit.tags")) {
                    return args_2_edit_tags.stream().filter(a -> a.startsWith(arg3)).toList();
                }
                else if(args[1].equalsIgnoreCase("edit.spawns")
                || args[1].equalsIgnoreCase("edit.generators")
                || args[1].equalsIgnoreCase("edit.pickups")) {
                    return args_list.stream().filter(a -> a.startsWith(arg3)).toList();
                }
            }
            if(args[0].equalsIgnoreCase("kits")) {
                if(args[1].equalsIgnoreCase("edit")) {
                    return args_2_kits_edit.stream().filter(a -> a.startsWith(arg3)).toList();
                }
            }
        }
        else if(args.length == 5) {
            String arg4 = args[4].toLowerCase();

            if(args[0].equalsIgnoreCase("kits")) {
                if(args[1].equalsIgnoreCase("edit")) {
                    if(args[3].equals("icon.type")) {
                        return materials()
                                .filter(m -> m.startsWith(arg4))
                                .toList();
                    }
                }
            }
            if(args[0].equals("config")) {
                if(args[1].equalsIgnoreCase("edit")) {
                    if(args[3].equalsIgnoreCase("shop-item")) {
                        return args_3_get_set.stream().filter(a -> a.startsWith(arg4)).toList();
                    }
                }
                else if(args[1].equalsIgnoreCase("edit.pickups")) {
                    if(args[2].equalsIgnoreCase("add")) {
                        return materials().filter(a -> a.startsWith(arg4)).toList();
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    protected boolean sendHelp(CommandSender sender) {
        sender.sendMessage("§a TODO help menu");
        return true;
    }

    protected String niceVector(Vector vector) {
        return "(" + vector.getX() + "," + vector.getY() + "," + vector.getZ() + ")";
    }

    private boolean saveConfiguration(CommandSender sender, WorldConfiguration config) {
        try{
            config.save();
            ReignOfCubes2.info("Configuration " + config.getName() + " saved successfully.");
            return true;
        } catch(IOException e) {
            ReignOfCubes2.error("Could not save " + config.getName() + ": " + e.getMessage());
            return error(sender,"Could not save " + config.getName() + ": " + e.getMessage());
        }
    }

    private Color parseColor(String arg) throws IllegalArgumentException {
        String[] rgb = arg.split(",", 3);
        if(rgb.length < 3)
            throw new IllegalArgumentException("Missing a component. Split RGB with comma.");
        int r,g,b;
        try {
            r = Integer.parseInt(rgb[0]);
            g = Integer.parseInt(rgb[1]);
            b = Integer.parseInt(rgb[2]);
            return Color.fromRGB(r, g, b);
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("Invalid color format ("+arg+")");
        }
    }

    private Stream<String> materials() {
        return Arrays.stream(Material.values())
                .map(Enum::name)
                .filter(m -> !m.startsWith("LEGACY_"))
                .map(String::toLowerCase);
    }

}
