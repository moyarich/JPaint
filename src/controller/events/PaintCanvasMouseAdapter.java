package controller.events;

import model.MoveShape;
import model.PaintObservable;
import model.commands.CommandHistory;
import model.interfaces.IApplicationState;
import model.mode.DrawMode;
import model.mode.SelectMode;
import model.mode.StartAndEndPointMode;
import view.gui.PaintCanvas;
import view.interfaces.PaintCanvasBase;
import javax.swing.SwingUtilities;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** One mouse gesture creates at most one history entry. */
public class PaintCanvasMouseAdapter extends MouseAdapter {
    private final PaintCanvasBase canvas;
    private final IApplicationState state;
    private Point start, previous;
    private MoveShape movement;
    private StartAndEndPointMode mode;

    public PaintCanvasMouseAdapter(PaintCanvasBase canvas, IApplicationState state) {
        this.canvas = canvas;
        this.state = state;
    }

    @Override public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) return;
        canvas.requestFocusInWindow();
        start = previous = e.getPoint();
        mode = state.getActiveStartAndEndPointMode();
        movement = mode == StartAndEndPointMode.MOVE
                ? new MoveShape(start, new Point(), canvas, state, new PaintObservable()) : null;
    }

    @Override public void mouseDragged(MouseEvent e) {
        if (start == null) return;
        if (movement != null) movement.translate(e.getX() - previous.x, e.getY() - previous.y);
        else if (canvas instanceof PaintCanvas) ((PaintCanvas) canvas).setPreview(start, e.getPoint(), state);
        previous = e.getPoint();
    }

    @Override public void mouseReleased(MouseEvent e) {
        if (start == null || !SwingUtilities.isLeftMouseButton(e)) return;
        if (canvas instanceof PaintCanvas) ((PaintCanvas) canvas).clearPreview();
        switch (mode) {
            case DRAW: new DrawMode(start, e.getPoint(), canvas, state).operate(); break;
            case SELECT: new SelectMode(start, e.getPoint(), canvas, state).operate(); break;
            case MOVE:
                movement.translate(e.getX() - previous.x, e.getY() - previous.y);
                movement.setEndPoint(e.getPoint());
                if (!start.equals(e.getPoint()) && movement.hasShapes()) CommandHistory.add(movement);
                break;
        }
        start = null;
        canvas.repaint();
    }
}
