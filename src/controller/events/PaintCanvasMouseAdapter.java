package controller.events;

import model.interfaces.IApplicationState;
import model.interfaces.IMode;
import model.mode.DrawMode;
import model.mode.MoveMode;
import model.mode.SelectMode;
import view.interfaces.PaintCanvasBase;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Controls the mouse effects
 * <p>
 * calls reprint() on:
 * mouseReleased
 * mouseDragged
 * mouseClicked
 * mouseMoved
 *
 * @author Moya Richards
 */
public class PaintCanvasMouseAdapter extends MouseAdapter {

    /**
     * For testing: draw a demo shape on the paint canvas
     */
    Shape drawDemoShape;
    private PaintCanvasBase paintCanvas;
    private Point startPoint, endPoint;
    private IApplicationState appState;
    private int dragStartX = 0;
    private int dragStartY = 0;
    private MoveMode moveMode = null;
    private boolean mouseDragged = false;

    public PaintCanvasMouseAdapter(PaintCanvasBase paintCanvas, IApplicationState appState) {
        this.paintCanvas = paintCanvas;
        this.appState = appState;
    }


    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = e.getPoint();
        endPoint = e.getPoint();


        dragStartX = e.getX();
        dragStartY = e.getY();

        mouseDragged = false;
        //------------------------
        System.out.println("===mousePressed " + e.getPoint() + " mode " + appState.getActiveStartAndEndPointMode());
        //------------------------
    }


    /**
     * Move the shape on mouse release
     *
     * @param e
     */
    public void mouseDragged(MouseEvent e) {

        mouseDragged = true;

        //------------------------
        System.out.println("===mouseDragged " + e.getPoint() + " mode " + appState.getActiveStartAndEndPointMode());
        //------------------------

        int currentX = e.getX();
        int currentY = e.getY();

        switch (appState.getActiveStartAndEndPointMode()) {
            case MOVE:

                int translateXX = currentX - dragStartX;
                int translateYY = currentY - dragStartY;
                Point transformPos = new Point(translateXX, translateYY);

                System.out.println("+++ =------ -dragStartX  " + dragStartX + " -dragStartY  " + dragStartY + " transformPos " + transformPos);

                /*
                 * dragStartX, dragStartY originally contained the mouse position when it was pressed
                 * now it must be reset to match the current movement of the mouse
                 */
                dragStartX = currentX;
                dragStartY = currentY;


                moveMode = new MoveMode(startPoint, transformPos, paintCanvas, appState);
                moveMode.operate();

                break;
        }
    }

    /**
     * Draw the shape on mouse release
     * Select multiple shapes on mouse release
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        //------------------------
        System.out.println("===mouseReleased " + e.getPoint() + " mode: " + appState.getActiveStartAndEndPointMode());
        //------------------------

        endPoint = e.getPoint();


        IMode mode = null;

        switch (appState.getActiveStartAndEndPointMode()) {
            case DRAW:
                mode = new DrawMode(startPoint, endPoint, paintCanvas, appState);
                break;
            case SELECT:
                mode = new SelectMode(startPoint, endPoint, paintCanvas, appState);
                break;
            case MOVE:
                if (mouseDragged && moveMode != null) {
                    moveMode.lockMovement(endPoint);
                }

                break;
        }

        if (mode != null) {
            mode.operate();
        }
    }
}
