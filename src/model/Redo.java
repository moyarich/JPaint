package model;

import model.commands.CommandHistory;
import view.interfaces.PaintCanvasBase;

public class Redo {
    PaintObservable paintObservable;
    private PaintCanvasBase paintCanvas;

    public Redo(PaintObservable paintObservable) {
        this.paintObservable = paintObservable;
    }

    public void operate() {
        CommandHistory.redo();
        paintObservable.notifyUpdate();
    }
}
