package fr.jamailun.reignofcubes2.commands.jamcmd;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Getter
public class JamContext {

    private final CommandSender sender;
    private final Command command;
    private final String label;
    private final ArgsList args;

    public JamContext(CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = new ArgsList(args);
    }


}
