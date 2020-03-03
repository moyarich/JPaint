package model.commands;

import model.SelectShape;
import model.collection.ShapeRepository;
import model.interfaces.ICommand;
import model.interfaces.IUndoable;

import java.awt.*;

/**
 * @author Moya Richards
 */
public class SelectShapeCommand implements ICommand {
    SelectShape selectShape;

    public SelectShapeCommand(SelectShape selectShape) {
        this.selectShape = selectShape;
    }

    @Override
    public void run() {
        selectShape.operate();

        boolean result = !ShapeRepository.selectedCollection.getList().isEmpty();
        if (result) {
            System.out.println("<<-- selected shape added to command");
        }
    }

}
