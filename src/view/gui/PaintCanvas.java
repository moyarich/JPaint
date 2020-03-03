package view.gui;

import model.collection.ShapeRepository;
import model.interfaces.IObserver;
import model.interfaces.IShape;
import view.interfaces.PaintCanvasBase;

import java.awt.*;
import java.util.List;

public class PaintCanvas extends PaintCanvasBase implements IObserver {


    /**
     * Cannot call getGraphics on a Component. It may return null or a Graphics object that is in any other way "invalid"
     *
     * @return Graphics2D
     */
    public Graphics2D getGraphics2D() {
        return (Graphics2D) getGraphics();
    }

    /**
     * Print the shapes in the list on the canvas
     *
     * @author Moya Richards
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        List<IShape> shapeCollectionList = ShapeRepository.shapeCollection.getList();
        List<IShape> selectedCollectionList = ShapeRepository.selectedCollection.getList();


        for (IShape shapeItem : shapeCollectionList) {
            //** Must reset Graphics2D, since the stored PaintCanvas was destroyed on repaint
            shapeItem.setGraphics2d(g2);

            //** draw the shape.  It is already transformed, if the "move command' was selected
            shapeItem.paintShapeOnCanvas();

            //highlight selected Shape
            if (selectedCollectionList.contains(shapeItem)) {
                shapeItem.highlightShape();
            }
        }

        System.out.println("---repaint() called--- shapeCollectionList: " + ShapeRepository.shapeCollection.size() + "  " + shapeCollectionList);
    }

    @Override
    public void update() {
        this.repaint();
    }
}
