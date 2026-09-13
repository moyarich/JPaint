package model.commands;

import model.UngroupShape;
import model.interfaces.ICommand;
import model.interfaces.IUndoable;

/**
 * @author Moya Richards
 */
public class UngroupShapeCommand implements ICommand, IUndoable {
    UngroupShape ungroupShape;

    public UngroupShapeCommand(UngroupShape groupShape) {
        this.ungroupShape = groupShape;
    }

    @Override
    public void run() {

        if (model.collection.ShapeRepository.selectedCollection.getList().stream()
                .noneMatch(s -> s instanceof model.GroupShape)) return;
        ungroupShape.ungroup();

        CommandHistory.add(this);
    }

    @Override
    public void undo() {
        ungroupShape.undo();

    }

    @Override
    public void redo() {
        ungroupShape.redo();

    }
}
