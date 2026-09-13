package model.persistence;

import java.awt.Color;
import model.ShapeShadingType;
import model.ShapeType;
import model.dialogs.DialogProvider;
import model.interfaces.IApplicationState;
import model.interfaces.IDialogProvider;
import model.mode.StartAndEndPointMode;
import view.interfaces.IUiModule;

import java.io.Serializable;

public class ApplicationState implements IApplicationState, Serializable {
    private static final long serialVersionUID = -5545483996576839008L;
    private final IUiModule uiModule;
    private final IDialogProvider dialogProvider;

    private ShapeType activeShapeType;
    private Color activePrimaryColor;
    private Color activeSecondaryColor;
    private ShapeShadingType activeShapeShadingType;
    private StartAndEndPointMode activeStartAndEndPointMode;

    public ApplicationState(IUiModule uiModule) {
        this.uiModule = uiModule;
        this.dialogProvider = new DialogProvider(this);
        setDefaults();
    }

    @Override
    public void setActiveShape() {
        activeShapeType = uiModule.getDialogResponse(dialogProvider.getChooseShapeDialog());
    }

    @Override
    public void setActivePrimaryColor() {
        activePrimaryColor = uiModule.getDialogResponse(dialogProvider.getChoosePrimaryColorDialog());
    }

    @Override
    public void setActiveSecondaryColor() {
        activeSecondaryColor = uiModule.getDialogResponse(dialogProvider.getChooseSecondaryColorDialog());
    }

    @Override
    public void setActiveShadingType() {
        activeShapeShadingType = uiModule.getDialogResponse(dialogProvider.getChooseShadingTypeDialog());
    }

    @Override
    public void setActiveStartAndEndPointMode() {
        activeStartAndEndPointMode = uiModule.getDialogResponse(dialogProvider.getChooseStartAndEndPointModeDialog());
    }

    @Override
    public ShapeType getActiveShapeType() {
        return activeShapeType;
    }

    @Override
    public Color getActivePrimaryColor() {
        return activePrimaryColor;
    }

    @Override
    public Color getActiveSecondaryColor() {
        return activeSecondaryColor;
    }

    @Override
    public ShapeShadingType getActiveShapeShadingType() {
        return activeShapeShadingType;
    }

    @Override
    public StartAndEndPointMode getActiveStartAndEndPointMode() {
        return activeStartAndEndPointMode;
    }

    private void setDefaults() {
        activeShapeType = ShapeType.ELLIPSE;
        activePrimaryColor = Color.BLUE;
        activeSecondaryColor = Color.GREEN;
        activeShapeShadingType = ShapeShadingType.FILLED_IN;
        activeStartAndEndPointMode = StartAndEndPointMode.DRAW;
    }


    /**
     * Display the parameters of the ApplicationState object
     * Author Moya Richards
     */
    @Override
    public String toString() {
        return "ApplicationState{" +
                "uiModule=" + uiModule +
                ", dialogProvider=" + dialogProvider +
                ", activeShapeType=" + activeShapeType +
                ", activePrimaryColor=" + activePrimaryColor +
                ", activeSecondaryColor=" + activeSecondaryColor +
                ", activeShapeShadingType=" + activeShapeShadingType +
                ", activeStartAndEndPointMode=" + activeStartAndEndPointMode +
                '}';
    }
}
