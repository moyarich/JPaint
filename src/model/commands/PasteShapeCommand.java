package model.commands;

import model.PasteShape;
import model.collection.ShapeRepository;
import model.interfaces.ICommand;
import model.interfaces.IUndoable;

/**
 * @author Moya Richards
 */
public class PasteShapeCommand implements ICommand, IUndoable {
    PasteShape pasteShape;

    public PasteShapeCommand(PasteShape pasteShape) {
        this.pasteShape = pasteShape;
    }

    @Override
    public void run() {


        boolean result = !ShapeRepository.clipboardShapeCollection.getList().isEmpty();
        if (result) {
            pasteShape.paste();
            System.out.println("<<-- pasted shape added to command");
            CommandHistory.add(this);
        }
    }

    @Override
    public void undo() {
        pasteShape.undo();
        System.out.println("<<-- pasted shape undo");
    }

    @Override
    public void redo() {
        pasteShape.redo();
        System.out.println("<<-- pasted shape redo");
    }
}
