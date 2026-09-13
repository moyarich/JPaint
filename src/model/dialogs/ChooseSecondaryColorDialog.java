package model.dialogs;

import java.awt.Color;
import model.interfaces.IApplicationState;
import view.interfaces.IDialogChoice;

public class ChooseSecondaryColorDialog implements IDialogChoice<Color> {

    private final IApplicationState applicationState;

    public ChooseSecondaryColorDialog(IApplicationState applicationState) {
        this.applicationState = applicationState;
    }

    @Override
    public String getDialogTitle() {
        return "Secondary Color";
    }

    @Override
    public String getDialogText() {
        return "Select a secondary color from the menu below:";
    }

    @Override
    public Color[] getDialogOptions() {
        return new Color[] {Color.BLACK, Color.WHITE, Color.BLUE, Color.RED, Color.GREEN};
    }

    @Override
    public Color getCurrentSelection() {
        return applicationState.getActiveSecondaryColor();
    }
}
