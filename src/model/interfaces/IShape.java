package model.interfaces;

import java.awt.*;
import java.util.List;

/**
 * Shape interface
 *
 * @author Moya Richards
 */
public interface IShape {
    void paintShapeOnCanvas();

    void setGraphics2d(Graphics2D graphics2d);

    void highlightShape();

    boolean detectCollision(Shape otherShape);

    void deleteShape();

    Shape getBoundingBox();

    IShape copyShape();

    IShape pasteShape();

    void create();

    void moveShape(int transformOffsetX, int transformOffsetY);

    List<IShape> getNodeList();


}

