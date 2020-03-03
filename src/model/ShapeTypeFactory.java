package model;

import model.interfaces.IShapeTypeStrategy;
import model.util.ShapeProperty;

import java.awt.*;

/**
 * Factory to create shape types from strategies
 *
 * @author Moya Richards
 */
public class ShapeTypeFactory {


    public static Shape build(ShapeProperty shapeProperty) {
        Shape shape = null;

        ShapeType shapeType = shapeProperty.getShapeType();
        Integer shapeXcoord = shapeProperty.getShapeXcoord();
        Integer shapeYcoord = shapeProperty.getShapeYcoord();
        Integer width = shapeProperty.getWidth();
        Integer height = shapeProperty.getHeight();
        Point startPoint = shapeProperty.getStartPoint();
        Point endPoint = shapeProperty.getEndPoint();


        /*
         * Get the type of shape to draw on the paint canvas
         */
        IShapeTypeStrategy shapeTypeStrategy = null;

        switch (shapeType) {
            case RECTANGLE:
                shapeTypeStrategy = new DesignRectangleShapeStrategy(shapeXcoord, shapeYcoord, width, height);
                break;
            case ELLIPSE:
                shapeTypeStrategy = new DesignEllipseShapeStrategy(shapeXcoord, shapeYcoord, width, height);
                break;
            case TRIANGLE:
                //shapeTypeStrategy = new DesignIsoscelesTriangleShapeStrategy(shapeXcoord, shapeYcoord, width, height);
                shapeTypeStrategy = new DesignRightTriangleShapeStrategy(startPoint, endPoint);
                break;
        }
        return shape = shapeTypeStrategy.design();
    }
}

