package model.commands;

import model.Undo;
import model.interfaces.ICommand;

public class UndoCommand implements ICommand {
    Undo undo;

    public UndoCommand(Undo undo) {
        this.undo = undo;
    }

    @Override
    public void run() {
        undo.operate();
    }
}
