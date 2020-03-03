package model.mode;

import model.DrawShape;
import model.ShapeAwtColor;
import model.ShapeShadingType;
import model.ShapeType;
import model.commands.DrawShapeCommand;
import model.interfaces.IApplicationState;
import model.interfaces.IMode;
import model.util.ShapeProperty;
import view.interfaces.PaintCanvasBase;

import java.awt.*;

public class DrawMode implements IMode {

    ShapeProperty shapeProperty;
    private Point startPoint;
    private Point endPoint;
    private PaintCanvasBase paintCanvas;
    private IApplicationState appState;

    public DrawMode(Point startPoint, Point endPoint, PaintCanvasBase paintCanvas, IApplicationState appState) {
        this.paintCanvas = paintCanvas;
        this.appState = appState;

        this.startPoint = startPoint;
        this.endPoint = endPoint;

        ShapeType shapeType = appState.getActiveShapeType();
        ShapeShadingType shadingType = appState.getActiveShapeShadingType();
        Color primaryColor = ShapeAwtColor.getColor(appState.getActivePrimaryColor());
        Color secondaryColor = ShapeAwtColor.getColor(appState.getActiveSecondaryColor());

        shapeProperty = new ShapeProperty(startPoint, endPoint);
        shapeProperty
                .setShapeType(shapeType)
                .setShadingType(shadingType)
                .setPrimaryColor(primaryColor)
                .setSecondaryColor(secondaryColor);
    }

    /**
     * Makes sure that a press and release on the same post of the canvas does not trigger the draw command
     */
    public void operate() {
        if (!startPoint.equals(endPoint)) {
            DrawShape drawShape = new DrawShape(paintCanvas, shapeProperty);
            DrawShapeCommand drawShapeCommand = new DrawShapeCommand(drawShape);
            drawShapeCommand.run();
        }
    }
}
