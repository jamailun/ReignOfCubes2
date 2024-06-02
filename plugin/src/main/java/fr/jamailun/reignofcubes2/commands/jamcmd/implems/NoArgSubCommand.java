package fr.jamailun.reignofcubes2.commands.jamcmd.implems;

import fr.jamailun.reignofcubes2.commands.jamcmd.JamContext;
import fr.jamailun.reignofcubes2.commands.jamcmd.JamSubCommand;

import java.util.Collections;
import java.util.List;

public abstract class NoArgSubCommand extends JamSubCommand {

    private final String label, description;

    public NoArgSubCommand(String label, String description) {
        this.label = label;
        this.description = description;
    }

    @Override
    public List<String> complete(JamContext context) {
        return Collections.emptyList();
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
