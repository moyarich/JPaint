package view.gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Shared studio palette and controls, independent of the OS button style. */
public final class StudioTheme {
    public static final Color INK = new Color(32, 35, 48);
    public static final Color ACCENT = new Color(103, 78, 230);
    public static final Color SURFACE = new Color(247, 247, 251);
    private StudioTheme() {}
    public static void install() {
        Font font = new Font("SansSerif", Font.PLAIN, 13);
        for (Object key : java.util.Collections.list(UIManager.getDefaults().keys())) {
            if (key.toString().endsWith(".font")) UIManager.put(key, font);
        }
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Label.foreground", INK);
        UIManager.put("TextField.selectionBackground", ACCENT);
        UIManager.put("Slider.background", Color.WHITE);
    }
    public static JButton button(String text) {
        JButton button = new JButton(text) {
            @Override protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean accent = Boolean.TRUE.equals(getClientProperty("accent"));
                Color base = accent ? ACCENT : SURFACE;
                if (getModel().isPressed()) base = accent ? ACCENT.darker() : new Color(222, 219, 244);
                else if (getModel().isRollover()) base = accent ? new Color(119, 95, 241) : new Color(236, 233, 252);
                g.setColor(base); g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                if (isFocusOwner()) {
                    g.setColor(ACCENT); g.setStroke(new BasicStroke(2));
                    g.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 14, 14);
                }
                g.dispose();
                setForeground(!isEnabled() ? new Color(155, 158, 172) : accent ? Color.WHITE : INK);
                super.paintComponent(graphics);
            }
        };
        button.setUI(new BasicButtonUI());
        button.setOpaque(false); button.setContentAreaFilled(false); button.setBorderPainted(false);
        button.setFocusPainted(false); button.setRolloverEnabled(true);
        button.setBorder(new EmptyBorder(12, 16, 12, 16));
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}
