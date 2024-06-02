package fr.jamailun.reignofcubes2.commands.jamcmd;

import fr.jamailun.reignofcubes2.commands.jamcmd.implems.NoArgSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class JamCommand extends JamHolder implements TabCompleter, CommandExecutor {

    private final PluginCommand pluginCommand;

    public JamCommand(String name) {
        pluginCommand = Bukkit.getPluginCommand(name);
        if(pluginCommand == null) {
            throw new RuntimeException("Unknown command '" + name + "'. Define it in plugin.yml.");
        }
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        JamContext context = new JamContext(sender, command, label, args);
        find(context).ifPresentOrElse(
                sub -> sub.command(context),
                () -> executeNoArg(context)
        );
        return true;
    }

    public abstract void executeNoArg(JamContext context);

    @Override
    public final @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        JamContext context = new JamContext(sender, command, label, args);
        Optional<JamSubCommand> sub = find(context);
        if(sub.isPresent())
            return sub.get().complete(context);
        return Collections.emptyList();
    }

    public List<String> generateHelp() {
        List<String> lines = new ArrayList<>();
        lines.add("§6Command '§a/" + pluginCommand.getLabel() + "§6' : §7" + pluginCommand.getDescription());
        if( ! subCommands.isEmpty()) {
            for(JamSubCommand sub : subCommands) {
                lines.addAll(sub.generateSubHelp(2));
            }
        }
        return lines;
    }

    public void addHelpSub() {
        addSubCommand(new NoArgSubCommand("help", "Display this menu") {
            @Override
            protected void executeNoArg(JamContext context) {
                info(context, generateHelp());
            }
        });
    }
}
