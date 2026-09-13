package controller;

import model.*;
import model.commands.*;
import view.EventName;
import view.interfaces.IUiModule;
import view.interfaces.PaintCanvasBase;

public class ButtonActions implements IJPaintController {
    private final IUiModule uiModule;
    PaintObservable paintObservable;
    private PaintCanvasBase paintCanvas;

    public ButtonActions(IUiModule uiModule, PaintCanvasBase paintCanvas, PaintObservable paintObservable) {
        this.uiModule = uiModule;
        this.paintCanvas = paintCanvas;
        this.paintObservable = paintObservable;
    }

    @Override
    public void setup() {
        setupEvents();
    }


    private void setupEvents() {
        uiModule.addEvent(EventName.UNDO, () -> processUndo());
        uiModule.addEvent(EventName.REDO, () -> processRedo());
        uiModule.addEvent(EventName.COPY, () -> processCopy());
        uiModule.addEvent(EventName.PASTE, () -> processPaste());
        uiModule.addEvent(EventName.DELETE, () -> processDelete());
        uiModule.addEvent(EventName.GROUP, () -> processGroup());
        uiModule.addEvent(EventName.UNGROUP, () -> processUngroup());
    }


    public void processUndo() {


        Undo undo = new Undo(paintObservable);
        UndoCommand undoCommand = new UndoCommand(undo);
        undoCommand.run();

    }

    public void processRedo() {


        Redo redo = new Redo(paintObservable);
        RedoCommand redoCommand = new RedoCommand(redo);
        redoCommand.run();

    }

    public void processCopy() {


        CopyShape copyShape = new CopyShape(paintCanvas);
        CopyShapeCommand copyShapeCommand = new CopyShapeCommand(copyShape);
        copyShapeCommand.run();
    }

    public void processPaste() {


        PasteShape pasteShape = new PasteShape(paintCanvas);
        PasteShapeCommand pasteShapeCommand = new PasteShapeCommand(pasteShape);
        pasteShapeCommand.run();
    }

    public void processDelete() {


        DeleteShape deleteShape = new DeleteShape(paintCanvas);
        DeleteShapeCommand deleteShapeCommand = new DeleteShapeCommand(deleteShape);
        deleteShapeCommand.run();
    }


    public void processGroup() {


        GroupShape groupShape = new GroupShape(paintCanvas);
        GroupShapeCommand groupShapeCommand = new GroupShapeCommand(groupShape);
        groupShapeCommand.run();
    }


    /**
     * Filter Ishape elements where the class type equals a GroupShape
     * Remove the Ishapes from the group
     */
    public void processUngroup() {


        UngroupShape ungroupShape = new UngroupShape(paintCanvas);
        UngroupShapeCommand ungroupShapeCommand = new UngroupShapeCommand(ungroupShape);
        ungroupShapeCommand.run();

    }
}
