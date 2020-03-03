package model;

import model.collection.ShapeRepository;
import model.interfaces.IApplicationState;
import model.interfaces.IObserver;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class MoveShape implements IUndoable {

    PaintObservable paintObservable;
    private Point startPoint;
    private Point endPoint = new Point();
    private Point transformOffset;
    private Graphics2D graphics2d;
    private PaintCanvasBase paintCanvas;
    private IApplicationState appState;

    public MoveShape(Point startPoint, Point transformOffset, PaintCanvasBase paintCanvas, IApplicationState appState, PaintObservable paintObservable) {
        this.graphics2d = paintCanvas.getGraphics2D();
        this.paintCanvas = paintCanvas;
        this.appState = appState;

        this.transformOffset = transformOffset;
        this.startPoint = startPoint;

        this.paintObservable = paintObservable;
        paintObservable.addObserver((IObserver) paintCanvas);
    }


    /**
     * Each time the mouse is dragged, the transform position gets incremented by a small value
     */
    public void move() {
        translate(transformOffset.x, transformOffset.y);
    }

    public void translate(int x, int y) {
        AffineTransform transform = new AffineTransform();


        //** the latest translation
        transform.translate(x, y);

        System.out.println("[[[[------------------------");
        System.out.println("startPoint  : " + startPoint);
        System.out.println("transformOffset - x: " + x + " y: " + y);

        for (IShape selectedShape : ShapeRepository.selectedCollection.getList()) {
            selectedShape.moveShape(x, y);
        }

        paintObservable.notifyUpdate();
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }


    /**
     * Undo uses the x and y position of the first "mouseClick" the startPoint
     * and the "mouseRelease" the endPoint
     * to calculate the shape's original position before it was dragged.
     * AffineTransform.transform() uses the offset calculated by subtracting the endPoint from the startPoint in order to change the x and y position of the shape's bounding box
     * <p>
     * ie:
     * startPoint  : Point[x=264,y=301]
     * endPoint    : Point[x=739,y=443]
     * <p>
     * transformOffset [x=-475,y=-142]
     * <p>
     * --- before transform bounding box ---:
     * Rectangle[x=555,y=175,width=118,height=137]
     * <p>
     * --- after transform bounding box (this is the original shape's bounding box)---:
     * <p>
     * Rectangle[x= (555-475), y=(175-142),width=118,height=137] = Rectangle[x=79,y=33,width=118,height=137]
     */
    @Override
    public void undo() {
        int offsetXX = startPoint.x - endPoint.x;
        int offsetYY = startPoint.y - endPoint.y;

        translate(offsetXX, offsetYY);
    }

    /**
     * Inverse operation of the Undo function
     * <p>
     * redo uses the x and y position of the first "mouseClick" the startPoint
     * and the "mouseRelease" the endPoint
     * to recalculate the shape's position to where it was previously dragged to.
     * AffineTransform.transform() uses the offset calculated by subtracting the startPoint  from the endPoint in order to change the x and y position of the shape's bounding box
     * <p>
     * ie:
     * startPoint  : Point[x=264,y=301]
     * endPoint    : Point[x=739,y=443]
     * <p>
     * transformOffset [x=475,y=142]
     * <p>
     * --- before transform bounding box ---:
     * Rectangle[x=79,y=33,width=118,height=137]
     * <p>
     * --- after transform bounding box (this is the original shape's bounding box)---:
     * <p>
     * Rectangle[x= (79+475), y=(33+142),width=118,height=137] = Rectangle[x=555,y=175,width=118,height=137]
     */
    @Override
    public void redo() {
        int translateXX = endPoint.x - startPoint.x;
        int translateYY = endPoint.y - startPoint.y;

        translate(translateXX, translateYY);
    }
}