package model.commands;

import model.CopyShape;
import model.interfaces.ICommand;

import java.awt.*;

/**
 * @author Moya Richards
 */
public class CopyShapeCommand implements ICommand {
    CopyShape copyShape;

    public CopyShapeCommand(CopyShape copyShape) {
        this.copyShape = copyShape;
    }

    @Override
    public void run() {
        copyShape.copy();
    }
}
