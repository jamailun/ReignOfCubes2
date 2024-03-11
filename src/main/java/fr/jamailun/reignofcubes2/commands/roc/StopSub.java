package fr.jamailun.reignofcubes2.commands.roc;

import fr.jamailun.reignofcubes2.commands.jamcmd.JamContext;
import fr.jamailun.reignofcubes2.commands.jamcmd.JamSubCommand;
import fr.jamailun.reignofcubes2.commands.jamcmd.implems.NoArgSubCommand;

import java.util.List;

public class StopSub extends NoArgSubCommand {

    StopSub() {
        super("stop", "Stop current game or countdown.");
    }

    @Override
    protected void executeNoArg(JamContext context) {
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
}
