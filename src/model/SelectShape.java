package model;

import model.collection.ShapeRepository;
import model.interfaces.IApplicationState;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SelectShape implements IUndoable {

    private Point startPoint, endPoint;


    private Graphics2D graphics2d;
    private PaintCanvasBase paintCanvas;
    private IApplicationState appState;

    public SelectShape(Point startPoint, Point endPoint, PaintCanvasBase paintCanvas, IApplicationState appState) {
        this.paintCanvas = paintCanvas;
        this.appState = appState;

        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }


    /**
     * Reset the selected shapes in the list
     * Add the selected shapes to the shapeCollection
     * Multi Selection occurs on - mouseReleased
     */
    public void operate() {
        ShapeRepository.selectedCollection.clear();

        List<IShape> shapeList = ShapeRepository.shapeCollection.getList();
        ArrayList<IShape> tempList = new ArrayList<>(shapeList);

        SelectBoundingBox selectBoundingBox = SelectBoundingBox.getInstance();
        selectBoundingBox.generateFromPoints(startPoint, endPoint);


        /*
         * Draw Outlined rectangular box - repaint happens really quickly so this might be pointless code
         */


        /*
         * Select All shapes within the bounding box to the select shape collection
         */

        Shape selectBoundingBoxShape = selectBoundingBox.getBoundingBox();

        for (IShape shape : shapeList) {
            if (shape.detectCollision(selectBoundingBoxShape)) {
                ShapeRepository.selectedCollection.add(shape);
            }
        }

        boolean result = !ShapeRepository.shapeCollection.getList().isEmpty();
        if (result) {





        }

        paintCanvas.repaint();
    }

    public void undo() {
        ShapeRepository.selectedCollection.clear();

        paintCanvas.repaint();
    }

    public void redo() {
        operate();

        paintCanvas.repaint();
    }
}
