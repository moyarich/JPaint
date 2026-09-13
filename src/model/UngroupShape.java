package model;

import model.collection.ShapeRepository;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class UngroupShape implements IUndoable {

    PaintCanvasBase paintCanvas;
    List<GroupShape> groupShapeCollectionList;


    public UngroupShape(PaintCanvasBase paintCanvas) {
        this.paintCanvas = paintCanvas;

        List<IShape> shapeCollectionList = ShapeRepository.selectedCollection.getList();

        SelectBoundingBox selectBoundingBox = SelectBoundingBox.getInstance();
        Shape selectBoundingBoxShape = selectBoundingBox.getBoundingBox();

        groupShapeCollectionList = shapeCollectionList.stream()
                .filter(ishape -> ishape instanceof GroupShape)
                .map(p -> (GroupShape) p)
                .collect(Collectors.toList());
    }

    public void ungroup() {
        for (GroupShape groupShape : groupShapeCollectionList) {
            groupShape.unGroup();
        }

        paintCanvas.repaint();
    }

    @Override
    public void undo() {
        for (GroupShape groupShape : groupShapeCollectionList) {
            groupShape.create();
        }
        paintCanvas.repaint();
    }

    @Override
    public void redo() {
        ungroup();
    }
}
