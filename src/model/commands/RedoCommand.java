package model.commands;

import model.Redo;
import model.interfaces.ICommand;

public class RedoCommand implements ICommand {
    Redo redo;

    public RedoCommand(Redo redo) {
        this.redo = redo;
    }

    @Override
    public void run() {
        redo.operate();
    }
}
