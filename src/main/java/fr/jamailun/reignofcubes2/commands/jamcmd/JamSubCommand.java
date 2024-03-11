package fr.jamailun.reignofcubes2.commands.jamcmd;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class JamSubCommand extends JamHolder {

    public void command(JamContext context) {
        find(context).ifPresentOrElse(
                sub -> sub.command(context),
                () -> executeNoArg(context)
        );
    }

    /**
     * No more argument.
     * @param context current execution context
     */
    protected abstract void executeNoArg(JamContext context);

    public abstract List<String> complete(JamContext context);

    public abstract String getLabel();

    public abstract String getDescription();

    public List<String> generateSubHelp(int indent) {
        List<String> lines = new ArrayList<>();
        String prefix = " ".repeat(indent);
        lines.add(prefix + "§b" + getLabel() + "§7" + getDescription());
        if( ! subCommands.isEmpty()) {
            for(JamSubCommand sub : subCommands) {
                lines.addAll(sub.generateSubHelp(indent + 2));
            }
        }
        return lines;
    }

}
