package model;

import model.interfaces.IShapeTypeStrategy;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Create the ellipse shape
 *
 * @author Moya Richards
 */
public class DesignEllipseShapeStrategy implements IShapeTypeStrategy {

    Integer shapeXcoord;
    Integer shapeYcoord;
    Integer width;
    Integer height;

    public DesignEllipseShapeStrategy(Integer shapeXcoord, Integer shapeYcoord, Integer width, Integer height) {
        this.shapeXcoord = shapeXcoord;
        this.shapeYcoord = shapeYcoord;
        this.width = width;
        this.height = height;
    }

    @Override
    public Shape design() {
        return new Ellipse2D.Double(shapeXcoord, shapeYcoord, width, height);
    }
}