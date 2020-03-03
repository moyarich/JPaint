package model;

import model.interfaces.IDrawable;

import java.awt.*;

/**
 * Create the command to draw a shape with a fill on the screen
 *
 * @author Moya Richards
 */
public class OutLineAndFilledShapeStrategy implements IDrawable {
    BasicStroke stroke = new BasicStroke(5.0f);
    Shape shape;
    Color primaryColor;
    Color secondaryColor;
    private Graphics2D graphics2d;


    public OutLineAndFilledShapeStrategy(Color primaryColor, Color secondaryColor, Shape shape, Graphics2D graphics2d) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.shape = shape;
        this.graphics2d = graphics2d;
    }


    @Override
    public void paintShape() {
        // Outlined
        graphics2d.setStroke(stroke);
        graphics2d.setPaint(secondaryColor);
        graphics2d.draw(shape);

        // Fill in shape
        graphics2d.setPaint(primaryColor);
        graphics2d.fill(shape);
    }

    public void setStoke(BasicStroke stroke) {
        this.stroke = stroke;
    }

    @Override
    public String toString() {
        return "OutlineAndFilledShape{" +
                "graphics2d=" + graphics2d +
                ", shape=" + shape.toString() +
                ", primaryColor=" + primaryColor.toString() +
                ", secondaryColor=" + secondaryColor.toString() +
                '}';
    }
}