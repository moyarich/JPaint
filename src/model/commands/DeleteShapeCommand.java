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
        if (model.collection.ShapeRepository.selectedCollection.size() == 0) return;
        deleteShape.delete();

        CommandHistory.add(this);
    }

    @Override
    public void undo() {
        deleteShape.undo();

    }

    @Override
    public void redo() {
        deleteShape.redo();

    }
}
