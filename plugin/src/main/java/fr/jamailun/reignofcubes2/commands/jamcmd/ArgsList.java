package fr.jamailun.reignofcubes2.commands.jamcmd;

import org.jetbrains.annotations.NotNull;

public class ArgsList {

    private final String[] args;
    private int index;

    public ArgsList(@NotNull String[] args) {
        this.args = args;
        index = 0;
    }

    public int size() {
        return args.length;
    }

    public String next() {
        return args[index++];
    }

    public String peek() {
        return args[index];
    }

    public int index() {
        return index;
    }

    public boolean hasMore() {
        return index < args.length - 1;
    }

    public void cancel() {
        index--;
    }

    public String get(int index) {
        return args[index];
    }

}
