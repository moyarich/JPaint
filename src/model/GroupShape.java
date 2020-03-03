/**
 * Copyright (c) 2019, Moya Richards. All rights reserved.
 */
package model;


import model.collection.ShapeRepository;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import model.util.ShapeBoundingBox;
import model.util.ShapeCollision;
import view.interfaces.PaintCanvasBase;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class GroupShape implements IShape, IUndoable {

    /**
     * The list of all the items in the group
     */
    List<IShape> groupedShapes = new ArrayList<>();
    private PaintCanvasBase paintCanvas;
    private Graphics2D graphics2d;
    /**
     * The group's bounding box
     */
    private Shape boundingBox;


    /**
     * Copy constructor
     * creates a new object is created from an existing object, as a copy of the existing object
     *
     * @param groupShape the object to copy
     */
    public GroupShape(GroupShape groupShape) {
        this.paintCanvas = groupShape.paintCanvas;
        this.graphics2d = groupShape.paintCanvas.getGraphics2D();

        for (IShape groupedShapes : groupShape.getList()) {
            this.add(groupedShapes.copyShape());
        }
        this.createBoundingBox();
    }

    public GroupShape(PaintCanvasBase paintCanvas) {
        this.paintCanvas = paintCanvas;
        this.graphics2d = paintCanvas.getGraphics2D();
    }

    /**
     * Add an item to the group
     *
     * @param iShape the group item to add
     */
    public void add(IShape iShape) {
        if (!groupedShapes.contains(iShape)) {
            groupedShapes.add(iShape);

            this.createBoundingBox();
        }
    }

    /**
     * Remove an item from the group
     *
     * @param iShape the group item to remove
     */
    public void remove(IShape iShape) {
        groupedShapes.add(iShape);
        this.createBoundingBox();
    }

    /**
     * The List of all the items within the group
     *
     * @return The list of items within the group
     */
    public List<IShape> getList() {
        return groupedShapes;
    }

    /**
     * Identify all the selected shapes and add them to a list within the group
     */
    public void group() {
        SelectBoundingBox selectBoundingBox = SelectBoundingBox.getInstance();

        List<IShape> selectedCollectionList = ShapeRepository.selectedCollection.getList();

        if (selectedCollectionList != null) {
            for (IShape iShape : selectedCollectionList) {
                this.add(iShape);
            }
            ShapeRepository.selectedCollection.removeAll(this.groupedShapes);
            ShapeRepository.selectedCollection.add(this);

            //** Add the group to the main shape list
            create();

            //** Draw the outline of the selected shapes boundingbox on the canvas
            selectBoundingBox.drawBoundingBox(graphics2d);

            //** Draw the outline of the group's boundingbox on the canvas
            drawBoundingBox();
        }
    }

    /**
     * Draw the outline of the bounding rectangles used to create the group on the canvas
     */
    public void drawBoundingBox() {
        graphics2d.setStroke(new BasicStroke(10.0f));
        graphics2d.setPaint(Color.MAGENTA);
        graphics2d.draw(this.getBoundingBox());
        graphics2d.setColor(Color.black);
        graphics2d.setFont(new Font("default", Font.BOLD, 16));
        graphics2d.drawString(" (Group bounding box)", this.getBoundingBox().getBounds().x, this.getBoundingBox().getBounds().y);
    }

    /**
     * Release the items from the group
     */
    public void unGroup() {
        for (IShape iShape : groupedShapes) {
            ShapeRepository.shapeCollection.add(iShape);
        }
        ShapeRepository.shapeCollection.remove(this);
        ShapeRepository.selectedCollection.remove(this);
    }

    /**
     * Flattens/un-group a group or subgroup returning
     * a list containing all the leaf nodes in the group
     *
     * @return the list of all (un-grouped) items
     */
    @Override
    public List<IShape> getNodeList() {
        List<IShape> newList = new ArrayList<>();

        for (IShape iShape : groupedShapes) {
            newList.addAll(iShape.getNodeList());
        }
        return newList;
    }

    /**
     * Add the group to the main shape list, ensuring that the group retains a proper tree like data structure
     * <p>
     * Create an hierarchical tree structure within the ShapeRepository.shapeCollection list
     * by adding the group to the ShapeRepository.shapeCollection and removing all the group's items from the ShapeRepository.shapeCollection list
     */
    @Override
    public void create() {
        ShapeRepository.shapeCollection.add(this);

        for (IShape iShape : this.groupedShapes) {
            //remove the individual shapes, add them as one unit
            ShapeRepository.shapeCollection.remove(iShape);
        }

        //** Recreate the group's bounding box

        this.createBoundingBox();
    }


    /**
     * Calculates the group's bounding box from all the items in the group
     */
    private void createBoundingBox() {
        List<Shape> groupShapeBounds = new ArrayList<>();

        for (IShape group : groupedShapes) {
            Shape rect = group.getBoundingBox();
            if (!groupShapeBounds.contains(rect)) {
                groupShapeBounds.add(group.getBoundingBox());
            }
        }

        ShapeBoundingBox shapeBoundingBox = new ShapeBoundingBox(groupShapeBounds);
        this.boundingBox = shapeBoundingBox.getBoundingBox();
    }


    /**
     * The bounding rectangle surrounding the group
     *
     * @return the bounding rectangle
     */
    @Override
    public Shape getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the Graphics2D object used to add items to the canvas
     *
     * @param graphics2d the graphics context
     */
    @Override
    public void setGraphics2d(Graphics2D graphics2d) {
        this.graphics2d = graphics2d;
    }

    /**
     * Paint all the items within the grouped onto the canvas
     * Every time paintCanvas.repaint() is called, a new graphics2d object is created,
     * therefore the graphics2d object has to be passed to the items in the group to
     * ensure that the items will get painted on the correct the graphics device
     */
    @Override
    public void paintShapeOnCanvas() {
        for (IShape iShape : groupedShapes) {
            iShape.setGraphics2d(graphics2d);
            iShape.paintShapeOnCanvas();
        }
    }


    /**
     * Highlight all the items in the group
     */
    @Override
    public void highlightShape() {
        for (IShape iShape : groupedShapes) {
            iShape.highlightShape();
        }
    }

    /**
     * Determines whether another shape has collided with this group.
     *
     * @param otherShape The shape use to detecting if there is an intersection between it and the group's bounding box
     * @return true if  a collision was detected, false if no collision was detected
     */
    @Override
    public boolean detectCollision(Shape otherShape) {

        createBoundingBox();
        boolean collisionDetected = ShapeCollision.detect(boundingBox, otherShape);

        System.out.println("<<-- group -- collisionDetected " + collisionDetected);
        return collisionDetected;
    }

    /**
     * Make a deep copy of the current Group , copies all the items in the group
     *
     * @return the copied group
     */
    @Override
    public IShape copyShape() {
        GroupShape gs = new GroupShape(this);
        return gs;
    }

    /**
     * Make a new group, then paste new items from the clipboard into the group
     *
     * @return the new group
     */
    @Override
    public IShape pasteShape() {
        GroupShape gs = new GroupShape(paintCanvas);
        for (IShape iShape : groupedShapes) {
            gs.add(iShape.pasteShape());
        }
        gs.create();
        return gs;
    }

    /**
     * Delete the items from the main shape list
     */
    @Override
    public void deleteShape() {
        unGroup();
        for (IShape iShape : groupedShapes) {
            iShape.deleteShape();
        }
    }

    /**
     * Move the items on the canvas
     *
     * @param transformOffsetX The number of points to move the shape along the x axis
     * @param transformOffsetY The number of points to move the shape along the y axis
     */
    @Override
    public void moveShape(int transformOffsetX, int transformOffsetY) {
        AffineTransform transform = new AffineTransform();

        //** move the bounding box a few points on the x and y axis
        transform.translate(transformOffsetX, transformOffsetY);

        //** create new shape by transforming the old shape

        boundingBox = transform.createTransformedShape(boundingBox);

        for (IShape iShape : groupedShapes) {
            iShape.moveShape(transformOffsetX, transformOffsetY);
        }
    }

    /**
     * Remove the grouping
     *
     * @see #unGroup()
     */
    @Override
    public void undo() {
        unGroup();
    }

    /**
     * Recreate the grouping that was removed
     *
     * @see #create()
     */
    @Override
    public void redo() {
        create();
    }

    @Override
    public String toString() {
        return "GroupShape{" +
                "\n\t, boundingBox=" + boundingBox +
                "\n\t, groupedShapes=\n\t" + groupedShapes.toString() +
                '}';
    }
}
