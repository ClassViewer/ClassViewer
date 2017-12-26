package org.glavo.viewer.gui;

public final class Command {
    private String[] args;

    public Command() {
        this(new String[0]);
    }

    public Command(String... args) {
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }
}
