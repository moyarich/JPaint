package model;

import model.interfaces.IShapeTypeStrategy;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Create the ellipse shape
 *
 * @author Moya Richards
 */
public class DesignRightTriangleShapeStrategy implements IShapeTypeStrategy {
    Shape shape;
    private Point startPoint, endPoint;

    public DesignRightTriangleShapeStrategy(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    @Override
    public Shape design() {

        Path2D triangle = new Path2D.Double();


        //** Place the first point of the triangle at the first x and y coordinate
        triangle.moveTo(startPoint.x, startPoint.y);

        //** Draw first line : a straight line
        triangle.lineTo(startPoint.x, endPoint.y);

        //** Draw second line: a straight line
        triangle.lineTo(endPoint.x, endPoint.y);

        //** Right triangle - the hypotenuse (long side) is the line between startPoint and endPoint
        triangle.closePath();


        System.out.println("path : " + this);
        System.out.println("triangle : " + triangle);


        return triangle;
    }

    @Override
    public String toString() {
        return "DesignRightTriangleShapeStrategy{" +
                "shape=" + shape +
                ", startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                '}';
    }
}