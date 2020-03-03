package model.interfaces;

import model.util.ShapeProperty;

import java.awt.*;

/**
 * Shape interface
 *
 * @author Moya Richards
 */
public interface IDrawShape {
    void designShape();

    void draw();

    Shape getShape();

    void setShape(Shape shape);

    ShapeProperty getShapeProperty();

    void updateShapeProperty(Shape newShape);

    //-----------------------------

    //public void create();
}

