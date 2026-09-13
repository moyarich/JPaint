package model;

import model.collection.ShapeRepository;
import model.interfaces.IApplicationState;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/** Keeps the moved objects, so later selection changes cannot redirect undo. */
public class MoveShape implements IUndoable {
    private final List<IShape> shapes = new ArrayList<>(ShapeRepository.selectedCollection.getList());
    private final Point startPoint, transformOffset;
    private Point endPoint;
    private final PaintCanvasBase canvas;

    public MoveShape(Point start, Point offset, PaintCanvasBase canvas,
                     IApplicationState state, PaintObservable observable) {
        this.startPoint = new Point(start);
        this.endPoint = new Point(start);
        this.transformOffset = new Point(offset);
        this.canvas = canvas;
    }
    public boolean hasShapes() { return !shapes.isEmpty(); }
    public void move() { translate(transformOffset.x, transformOffset.y); }
    public void translate(int x, int y) {
        for (IShape shape : shapes) shape.moveShape(x, y);
        canvas.repaint();
    }
    public void setEndPoint(Point end) { endPoint = new Point(end); }
    @Override public void undo() { translate(startPoint.x - endPoint.x, startPoint.y - endPoint.y); }
    @Override public void redo() { translate(endPoint.x - startPoint.x, endPoint.y - startPoint.y); }
}
