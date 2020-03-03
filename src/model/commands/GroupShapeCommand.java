package model.commands;

import model.GroupShape;
import model.collection.ShapeRepository;
import model.interfaces.ICommand;
import model.interfaces.IUndoable;

import java.awt.*;

/**
 * @author Moya Richards
 */
public class GroupShapeCommand implements ICommand, IUndoable {
    GroupShape groupShape;

    public GroupShapeCommand(GroupShape groupShape) {
        this.groupShape = groupShape;
    }

    @Override
    public void run() {
        boolean result = !ShapeRepository.selectedCollection.getList().isEmpty();
        if (result) {
            groupShape.group();
            System.out.println("<<-- groupd shape added to command");
            CommandHistory.add(this);
        }
    }

    @Override
    public void undo() {
        groupShape.undo();
        System.out.println("<<-- group shape undo");
    }

    @Override
    public void redo() {
        groupShape.redo();
        System.out.println("<<-- group  shape redo");
    }
}
