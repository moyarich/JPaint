package model;

import model.collection.ShapeRepository;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;

import java.util.ArrayList;
import java.util.List;


public class PasteShape implements IUndoable {

    List<IShape> shapeList = ShapeRepository.shapeCollection.getList();
    List<IShape> clipBoardList = ShapeRepository.clipboardShapeCollection.getList();
    List<IShape> pastedShapes = new ArrayList<>();
    private PaintCanvasBase paintCanvas;


    public PasteShape(PaintCanvasBase paintCanvas) {
        this.paintCanvas = paintCanvas;
    }

    public void paste() {
        pastedShapes.clear();
        for (IShape iShape : clipBoardList) {

            IShape pastedShape = iShape.pasteShape();

            shapeList.add(pastedShape);
            pastedShapes.add(pastedShape);
        }
        clipBoardList.clear();
        for (IShape shape : pastedShapes) clipBoardList.add(shape.copyShape());
        ShapeRepository.selectedCollection.clear();
        ShapeRepository.selectedCollection.addAll(pastedShapes);
        paintCanvas.repaint();
    }

    @Override
    public void undo() {
        for (IShape pastedShape : pastedShapes) {
            shapeList.remove(pastedShape);
            ShapeRepository.selectedCollection.remove(pastedShape);
        }
        paintCanvas.repaint();
    }

    @Override
    public void redo() {
        for (IShape pastedShape : pastedShapes) {
            shapeList.add(pastedShape);
        }
        paintCanvas.repaint();
    }
}
