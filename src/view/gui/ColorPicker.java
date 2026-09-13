package view.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class ColorPicker {
    private ColorPicker() {}
    public static String hex(Color color) { return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()); }
    public static Color parseHex(String text) {
        String value = text.trim();
        if (!value.matches("#?[0-9a-fA-F]{6}")) throw new IllegalArgumentException("Use six hex digits, for example #674EE6.");
        return new Color(Integer.parseInt(value.replace("#", ""), 16));
    }
    /** Reusable panel also supports offscreen visual verification. */
    public static final class Editor extends JPanel {
        private final JTextField hexField = new JTextField(9);
        private final JLabel preview = new JLabel(" ", SwingConstants.CENTER);
        private final JLabel error = new JLabel(" ");
        private final JSlider brightness = new JSlider(0, 100, 100);
        private final JSpinner[] rgb = new JSpinner[3];
        private final ColorWheel wheel;
        private boolean syncing;
        public Editor(Color initial) {
            super(new BorderLayout(24, 12));
            setBackground(Color.WHITE); setBorder(new EmptyBorder(12, 12, 12, 12));
            wheel = new ColorWheel(c -> sync(c, false));
            JPanel left = new JPanel(new BorderLayout(0, 10));
            left.add(wheel, BorderLayout.CENTER);
            JPanel value = new JPanel(new BorderLayout());
            value.add(new JLabel("Brightness"), BorderLayout.NORTH); value.add(brightness);
            brightness.getAccessibleContext().setAccessibleName("Brightness");
            left.add(value, BorderLayout.SOUTH); add(left, BorderLayout.CENTER);
            JPanel right = new JPanel(); right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
            right.setPreferredSize(new Dimension(208, 320));
            preview.setOpaque(true); preview.setMaximumSize(new Dimension(208, 70));
            preview.setPreferredSize(new Dimension(208, 70)); right.add(preview);
            right.add(Box.createVerticalStrut(20)); right.add(new JLabel("HEX COLOR"));
            hexField.setMaximumSize(new Dimension(208, 34)); right.add(Box.createVerticalStrut(8)); right.add(hexField);
            hexField.getAccessibleContext().setAccessibleName("Hex color");
            right.add(Box.createVerticalStrut(18));
            JPanel channels = new JPanel(new GridLayout(2, 3, 6, 5));
            for (String name : new String[]{"Red", "Green", "Blue"}) channels.add(new JLabel(name));
            for (int i=0;i<3;i++) {
                rgb[i] = new JSpinner(new SpinnerNumberModel(0,0,255,1));
                rgb[i].getAccessibleContext().setAccessibleName(new String[]{"Red","Green","Blue"}[i]);
                channels.add(rgb[i]);
                rgb[i].addChangeListener(e -> { if (!syncing) sync(new Color((int)rgb[0].getValue(),(int)rgb[1].getValue(),(int)rgb[2].getValue()), true); });
            }
            channels.setMaximumSize(new Dimension(208, 62)); right.add(channels);
            right.add(Box.createVerticalStrut(18)); right.add(new JLabel("PALETTE")); right.add(Box.createVerticalStrut(8));
            JPanel palette = new JPanel(new GridLayout(2, 5, 6, 6));
            for (String code : new String[]{"202330","FFFFFF","674EE6","EC4899","F43F5E","F59E0B","FACC15","22C55E","14B8A6","3B82F6"}) {
                Color color = parseHex(code);
                JButton swatch = StudioTheme.button("●");
                swatch.setIcon(new Icon() {
                    public int getIconWidth(){return 20;} public int getIconHeight(){return 20;}
                    public void paintIcon(Component c,Graphics g,int x,int y){g.setColor(color);g.fillOval(x,y,19,19);g.setColor(Color.LIGHT_GRAY);g.drawOval(x,y,19,19);}
                });
                swatch.setText(""); swatch.setBorder(new EmptyBorder(4,4,4,4));
                swatch.setToolTipText(hex(color)); swatch.getAccessibleContext().setAccessibleName(hex(color));
                swatch.addActionListener(e -> sync(color,true)); palette.add(swatch);
            }
            palette.setMaximumSize(new Dimension(208, 64)); right.add(palette);
            for (Component item : right.getComponents()) if (item instanceof JComponent) ((JComponent)item).setAlignmentX(Component.LEFT_ALIGNMENT);
            add(right,BorderLayout.EAST);
            error.setForeground(new Color(180,35,60)); add(error,BorderLayout.SOUTH);
            brightness.addChangeListener(e -> { if (!syncing) wheel.setBrightness(brightness.getValue()/100f); });
            hexField.addActionListener(e -> applyHex());
            sync(initial,true);
        }
        private void sync(Color color, boolean updateWheel) {
            syncing=true;
            if (updateWheel) wheel.setColor(color);
            hexField.setText(hex(color)); preview.setBackground(color);
            brightness.setValue(Math.round(Color.RGBtoHSB(color.getRed(),color.getGreen(),color.getBlue(),null)[2]*100));
            rgb[0].setValue(color.getRed()); rgb[1].setValue(color.getGreen()); rgb[2].setValue(color.getBlue());
            error.setText(" "); syncing=false;
        }
        private boolean applyHex() {
            try { sync(parseHex(hexField.getText()), true); return true; }
            catch (IllegalArgumentException ex) {error.setText(ex.getMessage()); return false;}
        }
        public Color getColor() { return parseHex(hexField.getText()); }
    }
    public static Color show(Component parent, String title, Color initial) {
        JDialog dialog = new JDialog(parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        Editor editor = new Editor(initial);
        JPanel root = new JPanel(new BorderLayout(0,16)); root.setBorder(new EmptyBorder(20,20,20,20));
        JLabel heading = new JLabel(title); heading.setFont(new Font("SansSerif",Font.BOLD,22));
        root.add(heading,BorderLayout.NORTH);root.add(editor,BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        JButton cancel = StudioTheme.button("Cancel"), apply = StudioTheme.button("Apply color");
        apply.putClientProperty("accent",true); actions.add(cancel);actions.add(apply);root.add(actions,BorderLayout.SOUTH);
        Color[] result={initial}; cancel.addActionListener(e -> dialog.dispose());
        apply.addActionListener(e -> { if(editor.applyHex()){result[0]=editor.getColor();dialog.dispose();} });
        dialog.setContentPane(root);dialog.getRootPane().setDefaultButton(apply);
        dialog.getRootPane().registerKeyboardAction(e -> dialog.dispose(),KeyStroke.getKeyStroke("ESCAPE"),JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.pack();dialog.setResizable(false);dialog.setLocationRelativeTo(parent);dialog.setVisible(true);
        return result[0];
    }
}
