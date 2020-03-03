package model.commands;

import model.DeleteShape;
import model.interfaces.ICommand;
import model.interfaces.IUndoable;

/**
 * @author Moya Richards
 */
public class DeleteShapeCommand implements ICommand, IUndoable {
    DeleteShape deleteShape;

    public DeleteShapeCommand(DeleteShape deleteShape) {
        this.deleteShape = deleteShape;
    }

    @Override
    public void run() {
        deleteShape.delete();
        System.out.println("<<-- deleted shape added to command");
        CommandHistory.add(this);
    }

    @Override
    public void undo() {
        deleteShape.undo();
        System.out.println("<<-- deleted shape undo");
    }

    @Override
    public void redo() {
        deleteShape.redo();
        System.out.println("<<-- deleted shape redo");
    }
}
