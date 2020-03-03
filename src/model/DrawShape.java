package model;

import model.collection.ShapeRepository;
import model.interfaces.*;
import model.util.ShapeCollision;
import model.util.ShapeProperty;
import view.interfaces.PaintCanvasBase;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class DrawShape implements IShape, IDrawShape, IUndoable {

    Shape shape = null;
    IShapeTypeStrategy shapeTypeStrategy = null;
    private PaintCanvasBase paintCanvas;
    private ShapeProperty shapeProperty;
    private Graphics2D graphics2d;

    /**
     * Copy Constructor
     * creates a new object is created from an existing object, as a copy of the existing object
     *
     * @param ds a reference to the DrawShape object to be copied
     * @param paintCanvas the specified Graphics context
     */
    public DrawShape(IDrawShape ds, PaintCanvasBase paintCanvas) {
        ShapeProperty dsShapeProp = ds.getShapeProperty();

        this.shapeProperty = new ShapeProperty(dsShapeProp.getStartPoint(), dsShapeProp.getEndPoint());

        shapeProperty
                .setShapeType(dsShapeProp.getShapeType())
                .setShadingType(dsShapeProp.getShadingType())
                .setPrimaryColor(dsShapeProp.getPrimaryColor())
                .setSecondaryColor(dsShapeProp.getSecondaryColor());


        this.paintCanvas = paintCanvas;
        this.graphics2d = paintCanvas.getGraphics2D();
        designShape();
    }


    public DrawShape(PaintCanvasBase paintCanvas, ShapeProperty shapeProperty) {
        this.paintCanvas = paintCanvas;
        this.graphics2d = paintCanvas.getGraphics2D();
        this.shapeProperty = shapeProperty;

        designShape();
    }

    /**
     * create the shape to be drawn on the canvas
     */
    public void designShape() {

        shape = ShapeTypeFactory.build(shapeProperty);
    }

    /**
     * Place the shape on the canvas
     * this method is called in response to a call to repaint
     */
    public void paintShapeOnCanvas() {

        ShapeShadingType shapeShadingType = shapeProperty.getShadingType();
        Color primaryColor = shapeProperty.getPrimaryColor();
        Color secondaryColor = shapeProperty.getSecondaryColor();

        IDrawable drawableShape = new NullShapeStrategy();

        //Draw the shapes on the paintCanvas
        switch (shapeShadingType) {
            case OUTLINE_AND_FILLED_IN:
                drawableShape = DrawableShapeFactory.outLineAndFilledShape(primaryColor, secondaryColor, shape, graphics2d);
                break;
            case FILLED_IN:
                drawableShape = DrawableShapeFactory.filledShape(primaryColor, shape, graphics2d);
                break;
            case OUTLINE:
                drawableShape = DrawableShapeFactory.outlineShape(primaryColor, shape, graphics2d);
                break;
        }

        drawableShape.paintShape();
    }

    /**
     * Clear all selected shapes before drawing new shape
     * repaint the screen
     */
    public void draw() {
        create();
        paintCanvas.repaint();
    }

    /**
     * add the shape to the ShapeRepository.shapeCollection list
     */

    @Override
    public void create() {
         //** Clear all selected shapes before drawing new shapes
        ShapeRepository.selectedCollection.clear();

        paintShapeOnCanvas();
        ShapeRepository.shapeCollection.add(this);
    }

    @Override
    public IShape copyShape() {
        IShape copiedShape = new DrawShape(this, paintCanvas);

        return copiedShape;
    }

    /**
     * create a new shape by transforming the old copied shape
     * Replace the shape in the clipboard with the offsetted shape so that each paste of that object will automatically have a new offset
     */
    @Override
    public IShape pasteShape() {

        DrawShape pastedShape = new DrawShape(this, paintCanvas);

        AffineTransform transform = new AffineTransform();

        //** move the copied IShaped a few points on the x and y axis
        transform.translate(80, 80);

        Shape offsetCopiedShape = transform.createTransformedShape(this.getShape());

        pastedShape.setShape(offsetCopiedShape);

        return (DrawShape)pastedShape;
    }

    /**
     *  create new shape by transforming the old shape
     *  the replaced shape object will automatically have a new offset derived from the mouse's position
     */
    @Override
    public void moveShape(int transformOffsetX, int transformOffsetY) {

        IShape moveShape = new DrawShape(this, paintCanvas);

        AffineTransform transform = new AffineTransform();

        //** move the copied IShaped a few points on the x and y axis
        transform.translate(transformOffsetX, transformOffsetY);

        Shape offsetShape = transform.createTransformedShape(this.getShape());

        this.setShape(offsetShape);

    }

    /**
     * Determines whether another shape has collided with this shape.
     *
     * @param otherShape The shape use to detecting if there is an intersection between it this shape
     * @return true if  a collision was detected, false if no collision was detected
     *
     *
     */
    public boolean detectCollision(Shape otherShape) {
        return ShapeCollision.detect(shape, otherShape);
    }


    /**
     * highlight selected Shape
     */
    public void highlightShape() {
        float[] dash = {7.0f, 7.0f};
        BasicStroke stroke = new BasicStroke(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f, dash, 0.0f);


        OutlineShapeStrategy drawableShape = new OutlineShapeStrategy(Color.yellow, shape, graphics2d);
        drawableShape.setStroke(stroke);
        drawableShape.paintShape();
    }

    public List<IShape> getNodeList() {
        List<IShape> newList = new ArrayList<>();
        newList.add(this);
        return newList;
    }

    public void setGraphics2d(Graphics2D graphics2d) {
        this.graphics2d = graphics2d;
    }

    public ShapeProperty getShapeProperty() {
        return shapeProperty;
    }

    public void setShapeProperty(ShapeProperty shapeProperty) {
        this.shapeProperty = shapeProperty;
    }

    public Shape getBoundingBox() {
        return shape.getBounds();
    }

    public Shape getShape() {
        return shape;
    }


    /**
     * Inject a new shape
     * @param shape
     */
    public void setShape(Shape shape) {
        this.shape = shape;

         //** Use the new shape to update the shape properties
        updateShapeProperty(shape);
    }

    /**
     * Use the shape that was previous drawn to keep the properties of this class in sync
     *
     * @param shp
     */
    public void updateShapeProperty(Shape shp) {
        Rectangle rect = shp.getBounds();
        Point startPoint1 = new Point(rect.x, rect.y);
        Point endPoint1 = new Point((rect.width + rect.x), (rect.height + rect.y));

        this.shapeProperty.setStartPoint(startPoint1);
        this.shapeProperty.setEndPoint(endPoint1);
    }

    @Override
    public void deleteShape() {
        ShapeRepository.shapeCollection.remove(this);
    }

    @Override
    public void undo() {
        deleteShape();
    }

    @Override
    public void redo() {
        create();
    }


    @Override
    public String toString() {
        return "DrawShape{" +
                "\n\t, paintCanvas=" + paintCanvas +
                "\n\t, shapeProperty=" + shapeProperty.toString() +
                "\n\t, shape=" + shape +
                "\n\t, shape bounds =" + shape.getBounds() +
                "\n}\n";
    }
}
