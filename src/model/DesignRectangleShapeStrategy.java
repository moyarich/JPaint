package model;

import model.interfaces.IShapeTypeStrategy;

import java.awt.*;
import java.awt.geom.Rectangle2D;


/**
 * Create the ellipse shape
 *
 * @author Moya Richards
 */
public class DesignRectangleShapeStrategy implements IShapeTypeStrategy {

    Integer shapeXcoord;
    Integer shapeYcoord;
    Integer width;
    Integer height;

    public DesignRectangleShapeStrategy(Integer shapeXcoord, Integer shapeYcoord, Integer width, Integer height) {
        this.shapeXcoord = shapeXcoord;
        this.shapeYcoord = shapeYcoord;
        this.width = width;
        this.height = height;
    }

    @Override
    public Shape design() {
        return new Rectangle2D.Double(shapeXcoord, shapeYcoord, width, height);
    }
}