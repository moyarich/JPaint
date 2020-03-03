package model;


import model.collection.ShapeRepository;
import model.interfaces.IShape;
import view.interfaces.PaintCanvasBase;

import java.util.List;

public class CopyShape {

    List<IShape> selectedList = ShapeRepository.selectedCollection.getList();
    List<IShape> clipBoardList = ShapeRepository.clipboardShapeCollection.getList();
    private PaintCanvasBase paintCanvas;


    public CopyShape(PaintCanvasBase paintCanvas) {
        this.paintCanvas = paintCanvas;
    }

    public void copy() {
        clipBoardList.clear();

        for (IShape selectedShape : selectedList) {
            IShape copiedShape = selectedShape.copyShape();
            clipBoardList.add(copiedShape);
        }
    }
}