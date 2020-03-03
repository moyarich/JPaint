package model;

import model.util.ShapeBoundingBox;

import java.awt.*;

/**
 * Used to store the bounding box used to create selections
 */
public class SelectBoundingBox {

    public static SelectBoundingBox instance;
    private Shape boundingBox;

    private SelectBoundingBox() {
    }

    public static SelectBoundingBox getInstance() {
        if (instance == null)
            instance = new SelectBoundingBox();
        return instance;
    }

    public Shape generateFromPoints(Point startPoint, Point endPoint) {
        ShapeBoundingBox shapeBoundingBox = new ShapeBoundingBox();
        this.boundingBox = shapeBoundingBox.generateFromPoints(startPoint, endPoint);

        return this.boundingBox;
    }

    public Shape getBoundingBox() {
        return boundingBox;
    }


    /**
     * Draw the selection bounding box on the canvas
     */
    public void drawBoundingBox(Graphics2D graphics2d) {
        graphics2d.setStroke(new BasicStroke(5.0f));
        graphics2d.setPaint(Color.green);
        graphics2d.draw(boundingBox);
        graphics2d.setColor(Color.black);
        graphics2d.setFont(new Font("default", Font.BOLD, 16));

        float alpha = 0.5f; //draw half transparent
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        graphics2d.setComposite(ac);

        graphics2d.drawString(" (Selection bounding box)", boundingBox.getBounds().x, boundingBox.getBounds().y);
    }
}