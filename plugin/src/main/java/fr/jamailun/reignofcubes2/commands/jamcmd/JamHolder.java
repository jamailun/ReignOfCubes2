package fr.jamailun.reignofcubes2.commands.jamcmd;

import fr.jamailun.reignofcubes2.commands.jamcmd.implems.NoArgSubCommand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class JamHolder {

    protected final List<JamSubCommand> subCommands = new ArrayList<>();

    protected void addSubCommand(@NotNull JamSubCommand sub) {
        subCommands.add(sub);
    }

    protected Optional<JamSubCommand> find(JamContext context) {
        if(context.getArgs().hasMore()) {
            String arg = context.getArgs().next();
            for(JamSubCommand sub : subCommands) {
                if(sub.getLabel().equals(arg)) {
                    return Optional.of(sub);
                }
            }
            context.getArgs().cancel();
        }
        return Optional.empty();
    }

    public void info(JamContext context, String message) {
        context.getSender().sendMessage("§eINFO §7 | " + message);
    }
    public void info(JamContext context, List<String> messages) {
        messages.forEach(m -> info(context, m));
    }

    public void success(JamContext context, String message) {
        context.getSender().sendMessage("§aSUCCESS §f | " + message);
    }
    public void success(JamContext context, List<String> messages) {
        messages.forEach(m -> success(context, m));
    }

    public void error(JamContext context, String message) {
        context.getSender().sendMessage("§4ERROR §f | §c" + message);
    }
    public void error(JamContext context, List<String> messages) {
        messages.forEach(m -> error(context, m));
    }

}
