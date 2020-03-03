package model.interfaces;

import java.awt.*;

public interface IBoundingBox {
    public Shape generateFromPoints(Point startPoint, Point endPoint);
    public Shape getBoundingBox();
}
