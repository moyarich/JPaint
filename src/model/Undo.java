package model;

import model.commands.CommandHistory;
import view.interfaces.PaintCanvasBase;

public class Undo {
    PaintObservable paintObservable;
    private PaintCanvasBase paintCanvas;

    public Undo(PaintObservable paintObservable) {
        this.paintObservable = paintObservable;
    }

    public void operate() {
        CommandHistory.undo();
        paintObservable.notifyUpdate();
    }
}
