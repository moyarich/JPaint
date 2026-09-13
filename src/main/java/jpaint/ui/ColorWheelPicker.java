package jpaint.ui;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import java.util.Optional;

/** Native JavaFX wheel with synchronized brightness, hex, RGB, and swatches. */
public final class ColorWheelPicker extends HBox {
    private final Canvas wheel=new Canvas(272,272);
    private final Slider brightness=new Slider(0,1,1);
    private final TextField hex=new TextField();
    private final Label error=new Label();
    private final Region swatch=new Region();
    private final Spinner<Integer>[] channels;
    private double hue,saturation,value=1;
    private boolean syncing;
    private WritableImage disk;
    private double diskBrightness=-1;
    public static String hex(Color c){return String.format("#%02X%02X%02X",Math.round(c.getRed()*255),Math.round(c.getGreen()*255),Math.round(c.getBlue()*255));}
    public static Color parse(String text){if(!text.trim().matches("#?[0-9a-fA-F]{6}"))throw new IllegalArgumentException("Enter six hex digits, such as #674EE6.");return Color.web("#"+text.trim().replace("#",""));}
    public static Color colorAt(double x,double y,double radius,double value){return Color.hsb((Math.toDegrees(Math.atan2(-y,x))+360)%360,Math.min(1,Math.hypot(x,y)/radius),value);}
    @SuppressWarnings("unchecked") public ColorWheelPicker(Color initial){
        setSpacing(28);setPadding(new Insets(10));
        wheel.setFocusTraversable(true);wheel.setAccessibleText("Color wheel. Arrow left/right changes hue; up/down changes saturation.");
        VBox left=new VBox(14,wheel,new Label("Brightness"),brightness);left.setPrefWidth(272);
        brightness.setAccessibleText("Brightness");brightness.setBlockIncrement(.02);
        channels=new Spinner[3];HBox rgb=new HBox(8);
        for(int i=0;i<3;i++){
            Spinner<Integer> spinner=new Spinner<>(0,255,0);spinner.setPrefWidth(78);spinner.setAccessibleText(new String[]{"Red","Green","Blue"}[i]);
            channels[i]=spinner;rgb.getChildren().add(new VBox(6,new Label(new String[]{"R","G","B"}[i]),spinner));
            spinner.valueProperty().addListener((o,a,b)->{if(!syncing)setColor(Color.rgb(channels[0].getValue(),channels[1].getValue(),channels[2].getValue()));});
        }
        swatch.setMinHeight(64);swatch.getStyleClass().add("color-preview");
        hex.setAccessibleText("Hex color");hex.setOnAction(e -> applyHex());
        TilePane palette=new TilePane(8,8);palette.setPrefColumns(5);
        for(String code:new String[]{"202330","FFFFFF","674EE6","EC4899","F43F5E","F59E0B","FACC15","22C55E","14B8A6","3B82F6"}){
            Button button=new Button();button.getStyleClass().add("palette-color");button.setStyle("-fx-background-color: #"+code+";");
            button.setAccessibleText("Color #"+code);button.setTooltip(new Tooltip("#"+code));button.setOnAction(e -> setColor(Color.web(code)));palette.getChildren().add(button);
        }
        error.getStyleClass().add("error");error.setWrapText(true);
        VBox right=new VBox(12,swatch,new Label("HEX COLOR"),hex,rgb,new Label("PALETTE"),palette,error);right.setPrefWidth(250);
        getChildren().addAll(left,right);
        wheel.setOnMousePressed(e -> {wheel.requestFocus();choose(e.getX(),e.getY());});
        wheel.setOnMouseDragged(e -> choose(e.getX(),e.getY()));
        wheel.setOnKeyPressed(e -> {
            if(e.getCode()==KeyCode.LEFT)hue=(hue+359)%360;
            else if(e.getCode()==KeyCode.RIGHT)hue=(hue+1)%360;
            else if(e.getCode()==KeyCode.UP)saturation=Math.min(1,saturation+.02);
            else if(e.getCode()==KeyCode.DOWN)saturation=Math.max(0,saturation-.02);
            else return;
            sync();e.consume();
        });
        brightness.valueProperty().addListener((o,a,b)->{if(!syncing){value=b.doubleValue();sync();}});
        setColor(initial);
    }
    private void choose(double x,double y){double dx=x-136,dy=y-136;hue=(Math.toDegrees(Math.atan2(-dy,dx))+360)%360;saturation=Math.min(1,Math.hypot(dx,dy)/126);sync();}
    public void setColor(Color color){hue=color.getHue();saturation=color.getSaturation();value=color.getBrightness();sync();}
    public Color color(){return Color.hsb(hue,saturation,value);}
    public boolean applyHex(){try{setColor(parse(hex.getText()));return true;}catch(IllegalArgumentException ex){error.setText(ex.getMessage());return false;}}
    private void sync(){
        syncing=true;Color c=color();hex.setText(hex(c));brightness.setValue(value);
        channels[0].getValueFactory().setValue((int)Math.round(c.getRed()*255));channels[1].getValueFactory().setValue((int)Math.round(c.getGreen()*255));channels[2].getValueFactory().setValue((int)Math.round(c.getBlue()*255));
        swatch.setStyle("-fx-background-color: "+hex(c)+"; -fx-background-radius: 12;");error.setText("");syncing=false;paint();
    }
    private void paint(){
        if(disk==null || diskBrightness!=value){disk=new WritableImage(272,272);for(int y=0;y<272;y++)for(int x=0;x<272;x++)if(Math.hypot(x-136,y-136)<=126)disk.getPixelWriter().setColor(x,y,colorAt(x-136,y-136,126,value));diskBrightness=value;}
        var g=wheel.getGraphicsContext2D();g.clearRect(0,0,272,272);g.drawImage(disk,0,0);
        double x=136+Math.cos(Math.toRadians(hue))*saturation*126,y=136-Math.sin(Math.toRadians(hue))*saturation*126;
        g.setStroke(Color.WHITE);g.setLineWidth(3);g.strokeOval(x-6,y-6,12,12);g.setStroke(Color.web("#202330"));g.setLineWidth(1);g.strokeOval(x-8,y-8,16,16);
    }
    public static Optional<Color> show(Window owner,String title,Color initial){
        Dialog<Color> dialog=new Dialog<>();dialog.initOwner(owner);dialog.setTitle(title);
        ColorWheelPicker editor=new ColorWheelPicker(initial);dialog.getDialogPane().setContent(editor);
        dialog.getDialogPane().getStylesheets().add(ColorWheelPicker.class.getResource("/jpaint/studio.css").toExternalForm());
        ButtonType apply=new ButtonType("Apply color",ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,apply);
        dialog.getDialogPane().lookupButton(apply).getStyleClass().add("primary");
        dialog.getDialogPane().lookupButton(apply).addEventFilter(javafx.event.ActionEvent.ACTION,e->{if(!editor.applyHex())e.consume();});
        dialog.setResultConverter(b -> b==apply?editor.color():null);return dialog.showAndWait();
    }
}
