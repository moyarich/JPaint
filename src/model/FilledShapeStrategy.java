package model;

import model.interfaces.IDrawable;

import java.awt.*;

/**
 * Create the command to draw a shape with a fill on the screen
 *
 * @author Moya Richards
 */
public class FilledShapeStrategy implements IDrawable {
    Color color;
    Shape shape;
    private Graphics2D graphics2d;


    public FilledShapeStrategy(Color color, Shape shape, Graphics2D graphics2d) {
        this.color = color;
        this.shape = shape;
        this.graphics2d = graphics2d;
    }

    @Override
    public void paintShape() {
        // Fill in shape
        graphics2d.setPaint(color);
        graphics2d.fill(shape);
    }

    @Override
    public String toString() {
        return "DrawShapeCommand{" +
                "graphics2d=" + graphics2d +
                ", color=" + color.toString() +
                ", shape=" + shape.toString() +
                '}';
    }
}
