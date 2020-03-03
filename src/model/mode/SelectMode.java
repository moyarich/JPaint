package model.mode;

import model.SelectShape;
import model.commands.SelectShapeCommand;
import model.interfaces.IApplicationState;
import model.interfaces.IMode;
import view.interfaces.PaintCanvasBase;

import java.awt.*;

public class SelectMode implements IMode {

    private Point startPoint;
    private Point endPoint;

    private PaintCanvasBase paintCanvas;
    private IApplicationState appState;

    public SelectMode(Point startPoint, Point endPoint, PaintCanvasBase paintCanvas, IApplicationState appState) {
        this.paintCanvas = paintCanvas;
        this.appState = appState;

        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public void operate() {
        SelectShape selectShape = new SelectShape(startPoint, endPoint, paintCanvas, appState);
        SelectShapeCommand selectShapeCommand = new SelectShapeCommand(selectShape);
        selectShapeCommand.run();
    }
}
