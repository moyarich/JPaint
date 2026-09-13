package view.gui;

import model.*;
import model.collection.ShapeRepository;
import model.interfaces.IApplicationState;
import model.interfaces.IObserver;
import model.interfaces.IShape;
import model.mode.StartAndEndPointMode;
import model.util.ShapeProperty;
import view.interfaces.PaintCanvasBase;
import java.awt.*;

public class PaintCanvas extends PaintCanvasBase implements IObserver {
    private Shape preview;
    private Color previewColor;
    public PaintCanvas() {
        setOpaque(true);
        setBackground(Color.WHITE);
        setFocusable(true);
        setPreferredSize(new Dimension(1600, 1000));
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        getAccessibleContext().setAccessibleName("Drawing canvas");
    }
    /** Legacy API; model operations no longer acquire a transient graphics context. */
    public Graphics2D getGraphics2D() { return (Graphics2D) getGraphics(); }
    public void setPreview(Point start, Point end, IApplicationState state) {
        ShapeProperty properties = new ShapeProperty(start, end);
        properties.setShapeType(state.getActiveStartAndEndPointMode() == StartAndEndPointMode.SELECT
                ? ShapeType.RECTANGLE : state.getActiveShapeType());
        preview = ShapeTypeFactory.build(properties);
        previewColor = state.getActiveStartAndEndPointMode() == StartAndEndPointMode.SELECT
                ? new Color(79, 70, 229) : state.getActivePrimaryColor();
        repaint();
    }
    public void clearPreview() { preview = null; repaint(); }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (IShape shape : ShapeRepository.shapeCollection.getList()) {
                shape.setGraphics2d(g2);
                shape.paintShapeOnCanvas();
            }
            for (IShape shape : ShapeRepository.selectedCollection.getList()) {
                shape.setGraphics2d(g2);
                shape.highlightShape();
            }
            if (preview != null) {
                g2.setColor(previewColor);
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10, new float[]{6, 4}, 0));
                g2.draw(preview);
            }
        } finally { g2.dispose(); }
    }
    @Override public void update() { repaint(); }
}
