package model.commands;

import model.MoveShape;
import model.collection.ShapeRepository;
import model.interfaces.ICommand;
import model.interfaces.IUndoable;

import java.awt.*;

/**
 * @author Moya Richards
 */
public class MoveShapeCommand implements ICommand, IUndoable {
    MoveShape moveShape;

    public MoveShapeCommand(MoveShape moveShape) {
        this.moveShape = moveShape;

    }

    @Override
    public void run() {
        boolean result = !ShapeRepository.selectedCollection.getList().isEmpty();
        if (result) {
            moveShape.move();
            System.out.println("<<-- moved shape added to command");
            CommandHistory.add(this);
        }
    }

    @Override
    public void undo() {
        moveShape.undo();
        System.out.println("<<-- move shape undo");
    }

    @Override
    public void redo() {
        moveShape.redo();
        System.out.println("<<-- move  shape redo");
    }
}
