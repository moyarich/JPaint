package model.commands;

import model.interfaces.ICommand;
import model.interfaces.IDrawShape;
import model.interfaces.IUndoable;

/**
 * Create the command to create a shape with a fill on the screen
 *
 * @author Moya Richards
 */
public class DrawShapeCommand implements ICommand, IUndoable {

    IDrawShape drawShape;

    public DrawShapeCommand(IDrawShape shapeCreator) {
        this.drawShape = shapeCreator;
    }

    @Override
    public void run() {
        drawShape.draw();
        CommandHistory.add(this);
    }

    @Override
    public void undo() {
        ((IUndoable) drawShape).undo();
    }

    @Override
    public void redo() {
        ((IUndoable) drawShape).redo();
    }
}
