package fr.jamailun.reignofcubes2.commands.roc;

import fr.jamailun.reignofcubes2.commands.jamcmd.JamCommand;
import fr.jamailun.reignofcubes2.commands.jamcmd.JamContext;
import fr.jamailun.reignofcubes2.commands.jamcmd.implems.NoArgSubCommand;

public class RocJamCommand extends JamCommand {

    public RocJamCommand() {
        super("roc");
        // Help
        addHelpSub();
        //


    }

    @Override
    public void executeNoArg(JamContext context) {
        //TODO send help
    }
}
