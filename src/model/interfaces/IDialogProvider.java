package model.interfaces;

import java.awt.Color;
import model.ShapeShadingType;
import model.ShapeType;
import model.mode.StartAndEndPointMode;
import view.interfaces.IDialogChoice;

public interface IDialogProvider {

    IDialogChoice<ShapeType> getChooseShapeDialog();

    IDialogChoice<Color> getChoosePrimaryColorDialog();

    IDialogChoice<Color> getChooseSecondaryColorDialog();

    IDialogChoice<ShapeShadingType> getChooseShadingTypeDialog();

    IDialogChoice<StartAndEndPointMode> getChooseStartAndEndPointModeDialog();
}
