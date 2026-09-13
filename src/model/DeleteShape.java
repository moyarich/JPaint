package model;

import model.collection.ShapeRepository;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;

import java.util.ArrayList;
import java.util.List;


public class DeleteShape implements IUndoable {

    private PaintCanvasBase paintCanvas;


    private final List<IShape> originalOrder = new ArrayList<>(ShapeRepository.shapeCollection.getList());
    private List<IShape> deletedShapes = new ArrayList<IShape>();

    public DeleteShape(PaintCanvasBase paintCanvas) {
        this.paintCanvas = paintCanvas;

        deletedShapes.addAll(ShapeRepository.selectedCollection.getList());

    }

    public void delete() {
        for (IShape toBeDeletedShape : deletedShapes) {
            toBeDeletedShape.deleteShape();
        }
        paintCanvas.repaint();
    }

    @Override
    public void undo() {

        ShapeRepository.shapeCollection.clear();
        ShapeRepository.shapeCollection.addAll(originalOrder);

        paintCanvas.repaint();
    }

    @Override
    public void redo() {
        delete();
    }
}
