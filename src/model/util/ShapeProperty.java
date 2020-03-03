package model.util;

import model.ShapeShadingType;
import model.ShapeType;

import java.awt.*;

/**
 * Uses the builder pattern
 * The shape's bounding box is a rectangle consisting of the
 * shapeXcoord, shapeYcoord,width,height
 * IE: x,y,width,height
 * <p>
 * to calculate from bounding box
 * left   : shapeXcoord = x
 * bottom : shapeYcoord = y
 * right  : endPoint.x  = (shapeXcoord + width)
 * top    : endPoint.y  = (shapeYcoord + height)
 */
public class ShapeProperty {
    private Point startPoint;
    private Point endPoint;

    private ShapeType shapeType;
    private ShapeShadingType shadingType;
    private Color primaryColor;
    private Color secondaryColor;

    private Integer shapeXcoord;
    private Integer shapeYcoord;
    private Integer width;
    private Integer height;


    public ShapeProperty(Point startPoint, Point endPoint) {


        this.startPoint = startPoint;
        this.endPoint = endPoint;

        //Set default property values
        this.shapeType = ShapeType.RECTANGLE;
        this.shadingType = ShapeShadingType.OUTLINE;
        this.primaryColor = Color.MAGENTA;
        this.secondaryColor = Color.red;

        calculateProperty();
    }

    public void calculateProperty() {
        this.shapeXcoord = Math.min(startPoint.x, endPoint.x);
        this.shapeYcoord = Math.min(startPoint.y, endPoint.y);

        //** calculating rectWidth from x coordinates
        this.width = (endPoint.x >= startPoint.x) ? endPoint.x - startPoint.x : startPoint.x - endPoint.x;

        //** calculating height from y coordinates
        this.height = (endPoint.y >= startPoint.y) ? endPoint.y - startPoint.y : startPoint.y - endPoint.y;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public ShapeProperty setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
        return this;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public ShapeProperty setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
        return this;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public ShapeProperty setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public ShapeShadingType getShadingType() {
        return shadingType;
    }

    public ShapeProperty setShadingType(ShapeShadingType shadingType) {
        this.shadingType = shadingType;
        return this;
    }

    public Color getPrimaryColor() {
        return primaryColor;
    }

    public ShapeProperty setPrimaryColor(Color primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }

    public ShapeProperty setSecondaryColor(Color secondaryColor) {
        this.secondaryColor = secondaryColor;
        return this;
    }

    public Integer getShapeXcoord() {
        return shapeXcoord;
    }

    public ShapeProperty setShapeXcoord(Integer shapeXcoord) {
        this.shapeXcoord = shapeXcoord;
        return this;
    }

    public Integer getShapeYcoord() {
        return shapeYcoord;
    }

    public ShapeProperty setShapeYcoord(Integer shapeYcoord) {
        this.shapeYcoord = shapeYcoord;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public ShapeProperty setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    public ShapeProperty setHeight(Integer height) {
        this.height = height;
        return this;
    }


    @Override
    public String toString() {
        return "ShapeProperty{" +
                "shapeType=" + shapeType +
                ", startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", shadingType=" + shadingType +
                ", primaryColor=" + primaryColor +
                ", secondaryColor=" + secondaryColor +
                ", shapeXcoord=" + shapeXcoord +
                ", shapeYcoord=" + shapeYcoord +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
