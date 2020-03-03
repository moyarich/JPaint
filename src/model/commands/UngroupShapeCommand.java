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

        ungroupShape.ungroup();
        System.out.println("<<-- groupd shape added to command");
        CommandHistory.add(this);
    }

    @Override
    public void undo() {
        ungroupShape.undo();
        System.out.println("<<-- group shape undo");
    }

    @Override
    public void redo() {
        ungroupShape.redo();
        System.out.println("<<-- group  shape redo");
    }
}
