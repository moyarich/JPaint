package model.interfaces;

/**
 * Create the redo command
 * this command is used to redo a command on the screen
 *
 * @author Moya Richards
 */
public interface IUndoable {
    void undo();

    void redo();
}
