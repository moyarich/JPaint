package model;

import model.interfaces.IShapeTypeStrategy;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Create the Isosceles Triangle shape
 *
 * @author Moya Richards
 */
public class DesignIsoscelesTriangleShapeStrategy implements IShapeTypeStrategy {
    Shape shape;
    Integer shapeXcoord;
    Integer shapeYcoord;
    Integer width;
    Integer height;

    public DesignIsoscelesTriangleShapeStrategy(Integer shapeXcoord, Integer shapeYcoord, Integer width, Integer height) {
        this.shapeXcoord = shapeXcoord;
        this.shapeYcoord = shapeYcoord;
        this.width = width;
        this.height = height;
    }

    @Override
    public Shape design() {
        Path2D triangle = new Path2D.Double();

        //Isosceles triangle - has two sides of equal length
        //** Place the first point of the triangle at the top-middle of the bounding box
        triangle.moveTo(shapeXcoord + (width / 2), shapeYcoord);
        triangle.lineTo(shapeXcoord, shapeYcoord + (height));
        triangle.lineTo(shapeXcoord + width, shapeYcoord + (height));
        triangle.closePath();

        return triangle;
    }
}