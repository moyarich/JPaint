package model.util;

import model.ShapeType;
import model.ShapeTypeFactory;
import model.interfaces.IBoundingBox;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShapeBoundingBox implements IBoundingBox {
    List<Shape> list;
    Shape boundingBox;

    public ShapeBoundingBox(List<Shape> list) {
        this.list = list;
        updateBoundingBox();
    }

    public ShapeBoundingBox() {
    }

    /**
     * The top left of a rect is (X, Y)
     * the bottom right is (X+Width, Y+Height)
     * <p>
     * The width of a rectangle is given by Right - Left. The height is given by Bottom - Top
     * Rect = (xMin, yMin, xMax - xMin, yMax - yMin);
     */
    public Shape generateFromList(List<Shape> shapeList) {
        List<Integer> listStartXCoord = new ArrayList<>();
        List<Integer> listStartYCoord = new ArrayList<>();

        List<Integer> listEndXCoord = new ArrayList<>();
        List<Integer> listEndYCoord = new ArrayList<>();

        for (Shape shape : shapeList) {
            Rectangle shapeBoundingRec = shape.getBounds();
            listStartXCoord.add(shapeBoundingRec.x);
            listStartYCoord.add(shapeBoundingRec.y);

            listEndXCoord.add(shapeBoundingRec.x + shapeBoundingRec.width);
            listEndYCoord.add(shapeBoundingRec.y + shapeBoundingRec.height);
        }

        int xMin = Collections.min(listStartXCoord);
        int yMin = Collections.min(listStartYCoord);
        int xMax = Collections.max(listEndXCoord);
        int yMax = Collections.max(listEndYCoord);


        Point startPoint = new Point(xMin, yMin);
        Point endPoint = new Point(xMax, yMax);

        return generateFromPoints(startPoint, endPoint);
    }

    public void updateBoundingBox() {
        this.boundingBox = generateFromList(list);
    }

    public Shape generateFromPoints(Point startPoint, Point endPoint) {
        ShapeProperty shapeProperty = new ShapeProperty(startPoint, endPoint);
        shapeProperty.setShapeType(ShapeType.RECTANGLE);

        Shape shape = ShapeTypeFactory.build(shapeProperty);

        return shape;
    }

    public Shape generateFromShape(Shape shape) {
        Rectangle orig = shape.getBounds();
        int x = orig.x;
        int y = orig.y;
        int width = orig.width;
        int height = orig.height;

        return new Rectangle2D.Double(x, y, width, height);
    }


    public Shape getBoundingBox() {
        return this.boundingBox;
    }


}
