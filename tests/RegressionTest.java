import controller.events.PaintCanvasMouseAdapter;
import model.*;
import model.collection.ShapeRepository;
import model.commands.*;
import model.mode.StartAndEndPointMode;
import model.persistence.ApplicationState;
import model.util.ShapeProperty;
import view.EventName;
import view.gui.PaintCanvas;
import view.interfaces.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class RegressionTest {
    static PaintCanvas canvas = new PaintCanvas();
    static class Ui implements IUiModule {
        Object answer;
        public void addEvent(EventName event, IEventCallback callback) {}
        @SuppressWarnings("unchecked") public <T> T getDialogResponse(IDialogChoice dialog) { return (T) answer; }
    }
    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    static DrawShape shape(int x) {
        DrawShape shape = new DrawShape(canvas, new ShapeProperty(new Point(x, 20), new Point(x + 40, 60)));
        new DrawShapeCommand(shape).run();
        return shape;
    }
    static void reset() {
        ShapeRepository.shapeCollection.clear();
        ShapeRepository.selectedCollection.clear();
        ShapeRepository.clipboardShapeCollection.clear();
        CommandHistory.getUndoStack().clear();
        CommandHistory.getRedoStack().clear();
    }
    static MouseEvent event(int type, int x, int y) {
        return new MouseEvent(canvas, type, 0, 0, x, y, 1, false, MouseEvent.BUTTON1);
    }
    public static void main(String[] args) throws Exception {
        javax.swing.SwingUtilities.invokeAndWait(RegressionTest::run);
        System.out.println("All JPaint regression checks passed.");
    }
    static void run() {
        reset();
        check(view.gui.ColorWheel.colorAt(100, 0, 100, 1).equals(Color.RED), "wheel red at right edge");
        check(view.gui.ColorWheel.colorAt(0, 0, 100, 1).equals(Color.WHITE), "wheel white center");
        check(view.gui.ColorWheel.colorAt(100, 0, 100, 0).equals(Color.BLACK), "wheel brightness zero");
        view.gui.ColorWheel wheel = new view.gui.ColorWheel(c -> {});
        wheel.setColor(new Color(0x674EE6));
        check(wheel.getColor().equals(new Color(0x674EE6)), "wheel custom color round trip");
        wheel.setSize(284,284);
        wheel.setColor(Color.WHITE);
        wheel.dispatchEvent(new MouseEvent(wheel, MouseEvent.MOUSE_PRESSED, 0, 0, 274,142,1,false,MouseEvent.BUTTON1));
        check(wheel.getColor().equals(Color.RED), "wheel mouse selects edge hue");
        wheel.dispatchEvent(new MouseEvent(wheel, MouseEvent.MOUSE_DRAGGED, 0, 0, 142,142,1,false,MouseEvent.BUTTON1));
        check(wheel.getColor().equals(Color.WHITE), "wheel drag selects center saturation");
        wheel.setBrightness(.5f);
        check(wheel.getColor().getRed()==128, "wheel brightness updates selected color");
        view.gui.ColorPicker.Editor editor = new view.gui.ColorPicker.Editor(new Color(0x674EE6));
        check(editor.getColor().equals(new Color(0x674EE6)), "picker initial color retained");
        Color custom = new Color(0x4F46E5);
        check(view.gui.ColorPicker.parseHex(" #4f46e5 ").equals(custom), "hex accepts custom colors");
        check(view.gui.ColorPicker.hex(custom).equals("#4F46E5"), "hex display");
        for (String invalid : new String[]{"red", "#123", "GG0000", "#12345678"}) {
            boolean rejected = false;
            try { view.gui.ColorPicker.parseHex(invalid); }
            catch (IllegalArgumentException expected) { rejected = true; }
            check(rejected, "invalid hex rejected");
        }
        Ui colorUi = new Ui();
        ApplicationState colors = new ApplicationState(colorUi);
        colorUi.answer = custom; colors.setActivePrimaryColor();
        colorUi.answer = new Color(0xF59E0B); colors.setActiveSecondaryColor();
        check(colors.getActivePrimaryColor().equals(custom), "custom primary retained");
        check(colors.getActiveSecondaryColor().equals(colorUi.answer), "independent secondary retained");
        new model.mode.DrawMode(new Point(10, 10), new Point(70, 70), canvas, colors).operate();
        DrawShape colored = (DrawShape) ShapeRepository.shapeCollection.get(0);
        check(colored.getShapeProperty().getPrimaryColor().equals(custom), "draw uses custom color");
        check(((DrawShape) colored.copyShape()).getShapeProperty().getPrimaryColor().equals(custom), "copy retains custom color");
        canvas.setSize(100, 100);
        BufferedImage colorImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D cg = colorImage.createGraphics(); canvas.paint(cg); cg.dispose();
        check(colorImage.getRGB(40, 40) == custom.getRGB(), "custom fill rendered");
        reset();
        DrawShape a = shape(20), b = shape(100);
        ShapeRepository.selectedCollection.add(a);
        Ui ui = new Ui();
        ApplicationState state = new ApplicationState(ui);
        ui.answer = StartAndEndPointMode.MOVE;
        state.setActiveStartAndEndPointMode();
        PaintCanvasMouseAdapter mouse = new PaintCanvasMouseAdapter(canvas, state);
        mouse.mousePressed(event(MouseEvent.MOUSE_PRESSED, 30, 30));
        mouse.mouseDragged(event(MouseEvent.MOUSE_DRAGGED, 40, 40));
        mouse.mouseDragged(event(MouseEvent.MOUSE_DRAGGED, 50, 50));
        mouse.mouseReleased(event(MouseEvent.MOUSE_RELEASED, 55, 55));
        check(a.getBoundingBox().getBounds().x == 45, "drag includes release offset exactly once");
        check(CommandHistory.getUndoStack().size() == 3, "one move history entry");
        ShapeRepository.selectedCollection.clear();
        ShapeRepository.selectedCollection.add(b);
        CommandHistory.undo();
        check(a.getBoundingBox().getBounds().x == 20 && b.getBoundingBox().getBounds().x == 100, "undo uses original selection");
        CommandHistory.redo();
        check(a.getBoundingBox().getBounds().x == 45, "redo restores move");
        DrawShape copy = (DrawShape) a.copyShape();
        check(copy.getBoundingBox().equals(a.getBoundingBox()), "copy retains moved geometry");
        ShapeRepository.selectedCollection.add(a);
        GroupShape group = new GroupShape(canvas);
        new GroupShapeCommand(group).run();
        check(ShapeRepository.shapeCollection.size() == 1, "group replaces children");
        new CopyShape(canvas).copy();
        check(ShapeRepository.shapeCollection.size() == 1, "copy is side-effect free");
        new PasteShapeCommand(new PasteShape(canvas)).run();
        check(ShapeRepository.shapeCollection.size() == 2, "group paste has no duplicates");
        CommandHistory.undo();
        check(ShapeRepository.shapeCollection.size() == 1, "undo removes pasted group");
        CommandHistory.redo();
        check(ShapeRepository.shapeCollection.size() == 2, "redo restores pasted group once");
        ShapeRepository.selectedCollection.clear();
        ShapeRepository.selectedCollection.add(group);
        new UngroupShapeCommand(new UngroupShape(canvas)).run();
        check(ShapeRepository.shapeCollection.contains(a), "ungroup follows selection");
        CommandHistory.undo();
        check(ShapeRepository.shapeCollection.contains(group), "undo ungroup");
        reset();
        a = shape(20); b = shape(100);
        ShapeRepository.selectedCollection.add(a);
        new DeleteShapeCommand(new DeleteShape(canvas)).run();
        check(ShapeRepository.selectedCollection.size() == 0, "delete clears stale selection");
        CommandHistory.undo();
        check(ShapeRepository.shapeCollection.get(0) == a, "delete undo preserves layer order");
        CommandHistory.undo();
        int redoCount = CommandHistory.getRedoStack().size();
        new DeleteShapeCommand(new DeleteShape(canvas)).run();
        check(CommandHistory.getRedoStack().size() == redoCount, "empty delete preserves redo");
        canvas.setSize(300, 200);
        BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        canvas.paint(graphics); graphics.dispose();
        check(image.getRGB(280, 180) == Color.WHITE.getRGB(), "canvas background is white");
        check(image.getRGB(20, 30) != Color.WHITE.getRGB(), "shapes render without live graphics context");
    }
}
