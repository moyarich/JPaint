package main;

import controller.ButtonActions;
import controller.IJPaintController;
import controller.JPaintController;
import controller.events.PaintCanvasMouseAdapter;
import model.PaintObservable;
import model.interfaces.IObserver;
import model.persistence.ApplicationState;
import view.gui.Gui;
import view.gui.GuiWindow;
import view.gui.PaintCanvas;
import view.interfaces.IGuiWindow;
import view.interfaces.IUiModule;
import view.interfaces.PaintCanvasBase;

public class Main {


    public static void main(String[] args) {
        PaintCanvasBase paintCanvas = new PaintCanvas();
        IGuiWindow guiWindow = new GuiWindow(paintCanvas);
        IUiModule uiModule = new Gui(guiWindow);
        ApplicationState appState = new ApplicationState(uiModule);
        IJPaintController controller = new JPaintController(uiModule, appState);


        PaintObservable paintObservable = new PaintObservable();
        paintObservable.addObserver((IObserver) paintCanvas);

        IJPaintController buttonActions = new ButtonActions(uiModule, paintCanvas, paintObservable);

        controller.setup();
        buttonActions.setup();

        /*
         * add MouseAdapter as both mouseListener and mouseMotionListener
         * https://stackoverflow.com/questions/5577619/why-are-mousedragged-events-not-received-when-using-mouseadapter
         */
        PaintCanvasMouseAdapter paintCanvasMouseAdapter = new PaintCanvasMouseAdapter(paintCanvas, appState);
        paintCanvas.addMouseListener(paintCanvasMouseAdapter);
        paintCanvas.addMouseMotionListener(paintCanvasMouseAdapter);
        //--------------------------------------------------------------

    }
}
