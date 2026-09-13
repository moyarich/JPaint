package view.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/** Hue is angle; saturation is distance from the center. Brightness is separate. */
public final class ColorWheel extends JPanel {
    private float hue, saturation, brightness = 1;
    private BufferedImage disk;
    private int diskSize;
    private float diskBrightness = -1;
    private final Consumer<Color> onChange;
    public ColorWheel(Consumer<Color> onChange) {
        this.onChange = onChange;
        setPreferredSize(new Dimension(284, 284));
        setOpaque(false); setFocusable(true);
        getAccessibleContext().setAccessibleName("Color wheel. Left and right change hue; up and down change saturation.");
        MouseAdapter mouse = new MouseAdapter() {
            public void mousePressed(MouseEvent e) { requestFocusInWindow(); choose(e.getX(), e.getY()); }
            public void mouseDragged(MouseEvent e) { choose(e.getX(), e.getY()); }
        };
        addMouseListener(mouse); addMouseMotionListener(mouse);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: hue = (hue + 359f/360) % 1; break;
                    case KeyEvent.VK_RIGHT: hue = (hue + 1f/360) % 1; break;
                    case KeyEvent.VK_UP: saturation = Math.min(1, saturation + .02f); break;
                    case KeyEvent.VK_DOWN: saturation = Math.max(0, saturation - .02f); break;
                    default: return;
                }
                changed(); e.consume();
            }
        });
    }
    public static Color colorAt(double dx, double dy, double radius, float brightness) {
        float hue = (float) ((Math.atan2(-dy, dx) / (2 * Math.PI) + 1) % 1);
        return Color.getHSBColor(hue, (float) Math.min(1, Math.hypot(dx, dy) / radius), brightness);
    }
    private void choose(int x, int y) {
        double dx = x - getWidth()/2.0, dy = y - getHeight()/2.0;
        hue = (float) ((Math.atan2(-dy, dx) / (2*Math.PI) + 1) % 1);
        saturation = (float) Math.min(1, Math.hypot(dx, dy) / radius());
        changed();
    }
    private int radius() { return Math.max(1, Math.min(getWidth(), getHeight())/2 - 10); }
    public void setColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hue = hsb[0]; saturation = hsb[1]; brightness = hsb[2]; repaint();
    }
    public void setBrightness(float value) { brightness = value; changed(); }
    public Color getColor() { return Color.getHSBColor(hue, saturation, brightness); }
    private void changed() { repaint(); onChange.accept(getColor()); }
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        int r = radius(), size = r*2;
        if (disk == null || diskSize != size || diskBrightness != brightness) {
            disk = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < size; y++) for (int x = 0; x < size; x++) {
                double d = Math.hypot(x-r, y-r);
                if (d <= r) disk.setRGB(x, y, colorAt(x-r, y-r, r, brightness).getRGB());
            }
            diskSize = size; diskBrightness = brightness;
        }
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(disk, getWidth()/2-r, getHeight()/2-r, null);
        int x = getWidth()/2 + (int)(Math.cos(hue*2*Math.PI)*saturation*r);
        int y = getHeight()/2 - (int)(Math.sin(hue*2*Math.PI)*saturation*r);
        g.setStroke(new BasicStroke(3)); g.setColor(Color.WHITE); g.drawOval(x-6,y-6,12,12);
        g.setStroke(new BasicStroke(1)); g.setColor(StudioTheme.INK); g.drawOval(x-8,y-8,16,16);
        if (isFocusOwner()) { g.setColor(StudioTheme.ACCENT); g.drawOval(getWidth()/2-r-4,getHeight()/2-r-4,size+8,size+8); }
        g.dispose();
    }
}
