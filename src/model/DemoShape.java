package model;

import model.interfaces.IDrawable;
import model.util.ShapeProperty;

import java.awt.*;

public class DemoShape {


    //debug code
    //------------------------------------------------------------
    public static Shape draw(Graphics2D graphics2d) {
        Point startPoint = new Point(5, 100);
        Point endPoint = new Point(120, 130);

        ShapeProperty shapeProperty = new ShapeProperty(startPoint, endPoint);
        shapeProperty
                .setShapeType(ShapeType.TRIANGLE)
                .setShadingType(ShapeShadingType.OUTLINE_AND_FILLED_IN)
                .setPrimaryColor(Color.MAGENTA)
                .setSecondaryColor(Color.red);

        Shape shape = ShapeTypeFactory.build(shapeProperty);

        graphics2d.setStroke(new BasicStroke(5.0f));
        IDrawable drawableShape = DrawableShapeFactory.outlineShape(Color.MAGENTA, shape, graphics2d);
        drawableShape.paintShape();


        System.out.println("shapeProperty" + shapeProperty.toString());
        System.out.println("shape.getBounds() " + shape.getBounds());

        return shape;
    }
}
