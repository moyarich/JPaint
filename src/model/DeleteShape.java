package model;

import model.collection.ShapeRepository;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;

import java.util.ArrayList;
import java.util.List;


public class DeleteShape implements IUndoable {

    private PaintCanvasBase paintCanvas;


    private List<IShape> deletedShapes = new ArrayList<IShape>();

    public DeleteShape(PaintCanvasBase paintCanvas) {
        this.paintCanvas = paintCanvas;

        deletedShapes.addAll(ShapeRepository.selectedCollection.getList());
        System.out.println("deleteShape constructor called");
    }

    public void delete() {
        for (IShape toBeDeletedShape : deletedShapes) {
            toBeDeletedShape.deleteShape();
        }
        paintCanvas.repaint();
    }

    @Override
    public void undo() {

        for (IShape deletedShape : deletedShapes) {
            deletedShape.create();
        }

        paintCanvas.repaint();
    }

    @Override
    public void redo() {
        delete();
    }
}
