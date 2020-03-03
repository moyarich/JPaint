package model.mode;

import model.MoveShape;
import model.PaintObservable;
import model.commands.MoveShapeCommand;
import model.interfaces.IApplicationState;
import model.interfaces.IMode;
import view.interfaces.PaintCanvasBase;

import java.awt.*;


public class MoveMode implements IMode {
    MoveShape moveShape;
    PaintObservable paintObservable = new PaintObservable();
    private Point startPoint;
    private Point endPoint;
    private Point transformPos;
    private PaintCanvasBase paintCanvas;
    private IApplicationState appState;


    public MoveMode(Point startPoint, Point transformPos, PaintCanvasBase paintCanvas, IApplicationState appState) {
        this.paintCanvas = paintCanvas;
        this.appState = appState;

        this.transformPos = transformPos;
        this.startPoint = startPoint;
    }

    public void operate() {
        moveShape = new MoveShape(startPoint, transformPos, paintCanvas, appState, paintObservable);
        moveShape.move();
    }

    /**
     * Record the mouse release position as the final point for the movement of the shape
     *
     * @param endPoint
     */
    public void lockMovement(Point endPoint) {
        moveShape.setEndPoint(endPoint);
        MoveShapeCommand moveShapeCommand = new MoveShapeCommand(moveShape);
        moveShapeCommand.run();
    }
}