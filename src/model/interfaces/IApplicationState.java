package model.interfaces;

import java.awt.Color;
import model.ShapeShadingType;
import model.ShapeType;
import model.mode.StartAndEndPointMode;

public interface IApplicationState {
    void setActiveShape();

    void setActivePrimaryColor();

    void setActiveSecondaryColor();

    void setActiveShadingType();

    void setActiveStartAndEndPointMode();

    ShapeType getActiveShapeType();

    Color getActivePrimaryColor();

    Color getActiveSecondaryColor();

    ShapeShadingType getActiveShapeShadingType();

    StartAndEndPointMode getActiveStartAndEndPointMode();
}
