package view.gui;

import model.GroupShape;
import model.collection.ShapeRepository;
import model.commands.CommandHistory;
import model.interfaces.IApplicationState;
import view.EventName;
import view.interfaces.IGuiWindow;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.EnumMap;
import java.util.Map;

public class GuiWindow extends JFrame implements IGuiWindow {
    private final Map<EventName, JButton> buttons = new EnumMap<>(EventName.class);
    private final JLabel status = new JLabel("Drag on the canvas to draw your first shape.");
    private final Color ink = StudioTheme.INK;
    private final Color muted = new Color(100, 116, 139);
    private IApplicationState state;
    private final Timer refreshTimer;

    public GuiWindow(JComponent canvas) {
        super("JPaint — Shape studio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setSize(1180, 800);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(237, 237, 244));
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        JLabel title = new JLabel("JPaint   /   Studio");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        title.setForeground(ink);
        header.add(title, BorderLayout.WEST);
        JLabel tagline = new JLabel("Untitled canvas     •     Local session");
        tagline.setForeground(muted);
        header.add(tagline, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(new EmptyBorder(22, 18, 22, 18));
        sidebar.setPreferredSize(new Dimension(260, 660));
        section(sidebar, "DRAWING TOOLS");
        addButton(sidebar, EventName.CHOOSE_START_POINT_ENDPOINT_MODE, "Mode", "Choose Draw, Select or Move");
        addButton(sidebar, EventName.CHOOSE_SHAPE, "Shape", "Choose the shape to draw");
        addButton(sidebar, EventName.CHOOSE_SHADING_TYPE, "Shading", "Choose fill and outline style");
        sidebar.add(Box.createVerticalStrut(18));
        section(sidebar, "APPEARANCE");
        addButton(sidebar, EventName.CHOOSE_PRIMARY_COLOR, "Primary", "Fill color, or stroke color for outline-only shapes");
        addButton(sidebar, EventName.CHOOSE_SECONDARY_COLOR, "Secondary", "Outline color when using fill and outline");
        sidebar.add(Box.createVerticalStrut(18));
        section(sidebar, "EDIT SELECTION");
        JPanel edits = new JPanel(new GridLayout(0, 2, 8, 8));
        edits.setOpaque(false);
        edits.setAlignmentX(Component.LEFT_ALIGNMENT);
        edits.setMaximumSize(new Dimension(224, 140));
        for (EventName event : new EventName[]{EventName.COPY, EventName.PASTE, EventName.GROUP, EventName.UNGROUP, EventName.DELETE})
            addButton(edits, event, label(event), label(event) + " selected shapes");
        sidebar.add(edits);
        sidebar.add(Box.createVerticalGlue());
        JLabel help = new JLabel("<html><b>Make something your own.</b><br><br>Drag to create a shape.<br>Select objects to arrange them.</html>");
        help.setForeground(muted);
        sidebar.add(help);
        JScrollPane tools = new JScrollPane(sidebar);
        tools.setBorder(null);
        tools.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(tools, BorderLayout.WEST);

        JPanel workspace = new JPanel(new BorderLayout(0, 12));
        workspace.setOpaque(false);
        workspace.setBorder(new EmptyBorder(20, 28, 24, 28));
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        addButton(toolbar, EventName.UNDO, "Undo", "Undo (⌘/Ctrl+Z)");
        addButton(toolbar, EventName.REDO, "Redo", "Redo (⌘/Ctrl+Shift+Z)");
        JLabel paper = new JLabel("    ARTBOARD     1600 × 1000 px");
        paper.setForeground(muted);
        toolbar.add(paper);
        workspace.add(toolbar, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(canvas);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(216, 215, 228)));
        JPanel paperFrame = new JPanel(new BorderLayout());
        paperFrame.setBackground(new Color(223, 222, 234));
        paperFrame.setBorder(new EmptyBorder(16, 16, 18, 16));
        paperFrame.add(scroll);
        workspace.add(paperFrame, BorderLayout.CENTER);
        root.add(workspace, BorderLayout.CENTER);
        status.setBorder(new EmptyBorder(12, 24, 12, 24));
        status.setForeground(muted);
        root.add(status, BorderLayout.SOUTH);

        int key = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        bind(EventName.UNDO, KeyEvent.VK_Z, key);
        bind(EventName.REDO, KeyEvent.VK_Z, key | InputEvent.SHIFT_DOWN_MASK);
        bind(EventName.REDO, KeyEvent.VK_Y, key);
        bind(EventName.COPY, KeyEvent.VK_C, key);
        bind(EventName.PASTE, KeyEvent.VK_V, key);
        bind(EventName.GROUP, KeyEvent.VK_G, key);
        bind(EventName.UNGROUP, KeyEvent.VK_G, key | InputEvent.SHIFT_DOWN_MASK);
        bind(EventName.DELETE, KeyEvent.VK_DELETE, 0);
        bind(EventName.DELETE, KeyEvent.VK_BACK_SPACE, 0);
        refreshTimer = new Timer(150, e -> refresh());
        refreshTimer.start();
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) { refreshTimer.stop(); }
        });
    }
    public void setApplicationState(IApplicationState state) { this.state = state; refresh(); }
    private static String label(Object value) {
        String text = value instanceof Enum ? ((Enum<?>) value).name() : value.toString();
        text = text.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
    private void section(JPanel panel, String text) {
        JLabel heading = new JLabel(text);
        heading.setForeground(muted);
        heading.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        panel.add(heading);
        panel.add(Box.createVerticalStrut(10));
    }
    private void addButton(JPanel panel, EventName event, String title, String tooltip) {
        JButton button = StudioTheme.button(title);
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        button.setForeground(ink);
        button.setBackground(new Color(248, 250, 252));
        button.setMargin(new Insets(9, 10, 9, 10));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(224, 46));
        button.setToolTipText(tooltip);
        if (event == EventName.CHOOSE_START_POINT_ENDPOINT_MODE) button.putClientProperty("accent", true);
        buttons.put(event, button);
        panel.add(button);
        if (panel.getLayout() instanceof BoxLayout) panel.add(Box.createVerticalStrut(7));
    }
    private void bind(EventName event, int key, int modifiers) {
        String id = event.name() + key;
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, modifiers), id);
        getRootPane().getActionMap().put(id, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { if (getButton(event).isEnabled()) getButton(event).doClick(); }
        });
    }
    private void colorButton(EventName event, String name, Color color) {
        JButton button = getButton(event);
        button.setText(name + ": " + ColorPicker.hex(color));
        button.setIcon(new Icon() {
            public int getIconWidth() { return 16; }
            public int getIconHeight() { return 16; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(color); g.fillRect(x, y, 15, 15);
                g.setColor(Color.GRAY); g.drawRect(x, y, 15, 15);
            }
        });
    }
    private void refresh() {
        int selected = ShapeRepository.selectedCollection.size();
        getButton(EventName.UNDO).setEnabled(!CommandHistory.getUndoStack().empty());
        getButton(EventName.REDO).setEnabled(!CommandHistory.getRedoStack().empty());
        getButton(EventName.COPY).setEnabled(selected > 0);
        getButton(EventName.DELETE).setEnabled(selected > 0);
        getButton(EventName.GROUP).setEnabled(selected > 1);
        getButton(EventName.UNGROUP).setEnabled(ShapeRepository.selectedCollection.getList().stream().anyMatch(s -> s instanceof GroupShape));
        getButton(EventName.PASTE).setEnabled(ShapeRepository.clipboardShapeCollection.size() > 0);
        if (state == null) return;
        getButton(EventName.CHOOSE_SHAPE).setText("Shape: " + label(state.getActiveShapeType()) + "  ▾");
        getButton(EventName.CHOOSE_START_POINT_ENDPOINT_MODE).setText("Mode: " + label(state.getActiveStartAndEndPointMode()) + "  ▾");
        getButton(EventName.CHOOSE_SHADING_TYPE).setText(label(state.getActiveShapeShadingType()) + "  ▾");
        colorButton(EventName.CHOOSE_PRIMARY_COLOR, "Primary", state.getActivePrimaryColor());
        colorButton(EventName.CHOOSE_SECONDARY_COLOR, "Secondary", state.getActiveSecondaryColor());
        status.setText(label(state.getActiveStartAndEndPointMode()) + "    /    " + ShapeRepository.shapeCollection.size()
                + " objects    •    " + selected + " selected    •    Unsaved session");
    }
    @Override public JButton getButton(EventName event) { return buttons.get(event); }
}
