package jpaint;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import jpaint.model.*;
import jpaint.ui.*;
import java.util.*;

public final class Main extends Application {
    private enum Mode { DRAW, SELECT, MOVE }
    private final Document document=new Document();
    private final Pane artboard=new Pane();
    private final Group artworkLayer=new Group(),selectionLayer=new Group(),previewLayer=new Group();
    private final Map<String,Button> actions=new HashMap<>();
    private final Label status=new Label();
    private final ComboBox<Artwork.Kind> shape=new ComboBox<>();
    private final ComboBox<Artwork.Shading> shading=new ComboBox<>();
    private Color primary=Color.web("#674EE6"),secondary=Color.web("#202330");
    private Mode mode=Mode.DRAW,gestureMode;
    private double startX,startY;
    private boolean dragging;
    private Scene scene;
    private Stage stage;
    private Button primaryButton,secondaryButton;
    @Override public void start(Stage stage){
        this.stage=stage;
        BorderPane root=new BorderPane();root.getStyleClass().add("studio");
        Label logo=new Label("JPaint");logo.getStyleClass().add("logo");
        Label subtitle=new Label("/   Studio");subtitle.getStyleClass().add("subtitle");
        Region spacer=new Region();HBox.setHgrow(spacer,Priority.ALWAYS);
        Label session=new Label("Untitled canvas  •  Local session");session.getStyleClass().add("muted");
        HBox header=new HBox(18,logo,subtitle,spacer,session);header.setAlignment(Pos.CENTER_LEFT);header.getStyleClass().add("header");root.setTop(header);
        VBox tools=new VBox(10);tools.getStyleClass().add("sidebar");tools.setPrefWidth(260);
        tools.getChildren().add(section("DRAWING TOOLS"));
        ToggleGroup modeGroup=new ToggleGroup();HBox modes=new HBox(6);
        for(Mode m:Mode.values()){
            ToggleButton button=new ToggleButton(label(m));button.setToggleGroup(modeGroup);button.setMaxWidth(Double.MAX_VALUE);HBox.setHgrow(button,Priority.ALWAYS);
            button.setSelected(m==Mode.DRAW);button.setOnAction(e->{button.setSelected(true);cancelGesture();mode=m;refresh();});modes.getChildren().add(button);
        }
        shape.getItems().addAll(Artwork.Kind.RECTANGLE,Artwork.Kind.ELLIPSE,Artwork.Kind.TRIANGLE);shape.setValue(Artwork.Kind.ELLIPSE);shape.setMaxWidth(Double.MAX_VALUE);shape.setAccessibleText("Shape");
        shading.getItems().addAll(Artwork.Shading.values());shading.setValue(Artwork.Shading.FILL);shading.setMaxWidth(Double.MAX_VALUE);shading.setAccessibleText("Shading");
        shape.setConverter(converter());shading.setConverter(converter());
        tools.getChildren().addAll(modes,new Label("Shape"),shape,new Label("Shading"),shading,section("APPEARANCE"));
        primaryButton=new Button();secondaryButton=new Button();primaryButton.setMaxWidth(Double.MAX_VALUE);secondaryButton.setMaxWidth(Double.MAX_VALUE);
        primaryButton.setOnAction(e->ColorWheelPicker.show(stage,"Primary color",primary).ifPresent(c->{primary=c;refresh();}));
        secondaryButton.setOnAction(e->ColorWheelPicker.show(stage,"Secondary color",secondary).ifPresent(c->{secondary=c;refresh();}));
        primaryButton.setTooltip(new Tooltip("Fill color, or stroke color for outline-only shapes"));secondaryButton.setTooltip(new Tooltip("Outline color with Fill and outline"));
        tools.getChildren().addAll(primaryButton,secondaryButton,section("EDIT SELECTION"));
        GridPane edits=new GridPane();edits.setHgap(8);edits.setVgap(8);
        String[] names={"Copy","Paste","Group","Ungroup","Delete","Select all"};
        Runnable[] commands={document::copy,document::paste,document::group,document::ungroup,document::delete,document::selectAll};
        for(int i=0;i<names.length;i++){Button button=action(names[i],commands[i]);button.setPrefWidth(108);edits.add(button,i%2,i/2);}
        tools.getChildren().add(edits);
        Label help=new Label("Drag to create a shape.\nSelect objects to arrange them.\n\nDrawings last for this session.");help.getStyleClass().add("muted");help.setWrapText(true);VBox.setMargin(help,new Insets(18,0,0,0));tools.getChildren().add(help);
        ScrollPane toolScroll=new ScrollPane(tools);toolScroll.setFitToWidth(true);toolScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);toolScroll.getStyleClass().add("tools-scroll");root.setLeft(toolScroll);
        artboard.setPrefSize(1600,1000);artboard.setMinSize(1600,1000);artboard.setMaxSize(1600,1000);artboard.getStyleClass().add("artboard");artboard.setClip(new Rectangle(1600,1000));
        artworkLayer.setMouseTransparent(true);selectionLayer.setMouseTransparent(true);previewLayer.setMouseTransparent(true);
        artboard.getChildren().addAll(artworkLayer,selectionLayer,previewLayer);artboard.setFocusTraversable(true);artboard.setAccessibleText("Drawing canvas");
        ScrollPane scroll=new ScrollPane(artboard);scroll.setPannable(false);scroll.getStyleClass().add("canvas-scroll");
        HBox toolbar=new HBox(8,action("Undo",document::undo),action("Redo",document::redo),new Label("    ARTBOARD   1600 × 1000 px"));toolbar.setAlignment(Pos.CENTER_LEFT);
        BorderPane workspace=new BorderPane(scroll);workspace.setTop(toolbar);BorderPane.setMargin(toolbar,new Insets(0,0,18,0));workspace.getStyleClass().add("workspace");root.setCenter(workspace);
        status.getStyleClass().add("status");root.setBottom(status);
        scene=new Scene(root,1180,800);scene.getStylesheets().add(getClass().getResource("/jpaint/studio.css").toExternalForm());
        bindKeys();bindCanvas();stage.setScene(scene);stage.setTitle("JPaint — JavaFX Studio");stage.setMinWidth(850);stage.setMinHeight(650);refresh();stage.show();
        stage.focusedProperty().addListener((o,a,b)->{if(!b)cancelGesture();});
        if(getParameters().getRaw().contains("--smoke-test")) Platform.runLater(()->smokeTest());
    }
    private static String label(Enum<?> e){String text=e.name().toLowerCase().replace('_',' ');return Character.toUpperCase(text.charAt(0))+text.substring(1);}
    private static <T extends Enum<T>> javafx.util.StringConverter<T> converter(){return new javafx.util.StringConverter<>(){public String toString(T e){return e==null?"":label(e);}public T fromString(String s){throw new UnsupportedOperationException();}};}
    private Label section(String text){Label label=new Label(text);label.getStyleClass().add("section");return label;}
    private Button action(String title,Runnable command){Button button=new Button(title);button.setOnAction(e->{cancelGesture();command.run();refresh();});actions.put(title,button);return button;}
    private void bindKeys(){
        scene.addEventFilter(KeyEvent.KEY_PRESSED,e->{
            if(scene.getFocusOwner() instanceof TextInputControl)return;
            String command=null;
            if(e.isShortcutDown())command=switch(e.getCode()){
                case Z -> e.isShiftDown()?"Redo":"Undo";case Y -> "Redo";case C -> "Copy";case V -> "Paste";
                case G -> e.isShiftDown()?"Ungroup":"Group";case A -> "Select all";default -> null;
            };
            else if(e.getCode()==KeyCode.DELETE || e.getCode()==KeyCode.BACK_SPACE)command="Delete";
            else if(e.getCode()==KeyCode.ESCAPE){cancelGesture();document.select(Set.of());refresh();e.consume();}
            if(command!=null){actions.get(command).fire();e.consume();}
        });
    }
    private Artwork draft(double x,double y){return Artwork.shape(shape.getValue(),startX,startY,x,y,ColorWheelPicker.hex(primary),ColorWheelPicker.hex(secondary),shading.getValue());}
    private double clamp(double value,double max){return Math.max(0,Math.min(max,value));}
    private void bindCanvas(){
        artboard.setOnMousePressed(e->{if(e.getButton()!=MouseButton.PRIMARY)return;artboard.requestFocus();dragging=true;gestureMode=mode;startX=clamp(e.getX(),1600);startY=clamp(e.getY(),1000);});
        artboard.setOnMouseDragged(e->{if(!dragging)return;double x=clamp(e.getX(),1600),y=clamp(e.getY(),1000);previewLayer.getChildren().clear();
            if(gestureMode==Mode.DRAW){Node node=ArtworkView.create(draft(x,y));node.setOpacity(.55);previewLayer.getChildren().add(node);}
            else if(gestureMode==Mode.SELECT){Rectangle box=box(Math.min(startX,x),Math.min(startY,y),Math.abs(x-startX),Math.abs(y-startY));box.setFill(Color.web("#674ee6",.08));previewLayer.getChildren().add(box);}
            else {for(Node node:artworkLayer.getChildren())if(document.selection().contains(node.getUserData())){node.setTranslateX(x-startX);node.setTranslateY(y-startY);}selectionLayer.setTranslateX(x-startX);selectionLayer.setTranslateY(y-startY);}
        });
        artboard.setOnMouseReleased(e->{if(!dragging || e.getButton()!=MouseButton.PRIMARY)return;double x=clamp(e.getX(),1600),y=clamp(e.getY(),1000);
            if(gestureMode==Mode.DRAW)document.add(draft(x,y));
            else if(gestureMode==Mode.SELECT){if(Math.hypot(x-startX,y-startY)<3)document.selectAt(x,y);else document.selectRegion(startX,startY,x,y);}
            else document.move(x-startX,y-startY);
            dragging=false;refresh();
        });
    }
    private void cancelGesture(){if(dragging){dragging=false;refresh();}}
    private Rectangle box(double x,double y,double w,double h){Rectangle r=new Rectangle(x,y,w,h);r.setFill(Color.TRANSPARENT);r.setStroke(Color.web("#674ee6"));r.setStrokeWidth(1.5);r.getStrokeDashArray().addAll(5d,4d);return r;}
    private void colorButton(Button button,String title,Color color){Region dot=new Region();dot.setPrefSize(18,18);dot.setMinSize(18,18);dot.setMaxSize(18,18);dot.setStyle("-fx-background-color: "+ColorWheelPicker.hex(color)+"; -fx-background-radius: 5; -fx-border-color: #cfcfda; -fx-border-radius: 5;");button.setGraphic(dot);button.setText(title+"   "+ColorWheelPicker.hex(color));}
    private void refresh(){
        artworkLayer.getChildren().clear();selectionLayer.getChildren().clear();previewLayer.getChildren().clear();selectionLayer.setTranslateX(0);selectionLayer.setTranslateY(0);
        for(Artwork a:document.items()){Node node=ArtworkView.create(a);node.setUserData(a.id());artworkLayer.getChildren().add(node);if(document.selection().contains(a.id())){var b=a.bounds();selectionLayer.getChildren().add(box(b.left()-4,b.top()-4,b.width()+8,b.height()+8));}}
        actions.get("Undo").setDisable(!document.canUndo());actions.get("Redo").setDisable(!document.canRedo());actions.get("Paste").setDisable(!document.canPaste());
        actions.get("Copy").setDisable(document.selection().isEmpty());actions.get("Delete").setDisable(document.selection().isEmpty());actions.get("Group").setDisable(document.selection().size()<2);actions.get("Ungroup").setDisable(!document.canUngroup());actions.get("Select all").setDisable(document.items().isEmpty());
        colorButton(primaryButton,"Primary",primary);colorButton(secondaryButton,"Secondary",secondary);
        status.setText(label(mode)+"    /    "+document.items().size()+" objects    •    "+document.selection().size()+" selected    •    Unsaved session");artboard.setCursor(mode==Mode.MOVE?Cursor.MOVE:Cursor.CROSSHAIR);
    }
    private void smokeMouse(javafx.event.EventType<MouseEvent> type,double x,double y){
        var event=new MouseEvent(artboard,artboard,type,x,y,x,y,MouseButton.PRIMARY,1,
                false,false,false,false,type!=MouseEvent.MOUSE_RELEASED,false,false,false,false,false,
                new PickResult(artboard,x,y));
        javafx.event.Event.fireEvent(artboard,event);
    }
    private void smokeTest(){
        try{
            document.add(Artwork.shape(Artwork.Kind.ELLIPSE,100,100,280,260,"#674EE6","#202330",Artwork.Shading.FILL));document.selectAll();document.move(25,30);document.copy();document.paste();refresh();
            if(artworkLayer.getChildren().size()!=2)throw new AssertionError("Rendering failed");
            if (!ColorWheelPicker.hex(ColorWheelPicker.colorAt(126,0,126,1)).equals("#FF0000")) throw new AssertionError("Wheel hue");
            if (!ColorWheelPicker.hex(ColorWheelPicker.colorAt(0,0,126,1)).equals("#FFFFFF")) throw new AssertionError("Wheel saturation");
            ColorWheelPicker picker=new ColorWheelPicker(primary);
            if(!ColorWheelPicker.hex(picker.color()).equals(ColorWheelPicker.hex(primary)))throw new AssertionError("Picker round trip");
            scene.getRoot().applyCss();scene.getRoot().layout();
            var image=scene.snapshot(null);
            if(image.getWidth()<800)throw new AssertionError("Layout failed");
            SnapshotWriter.write(image,"build/javafx-window.ppm");
            Scene pickerScene=new Scene(picker);pickerScene.getStylesheets().add(getClass().getResource("/jpaint/studio.css").toExternalForm());
            Stage pickerStage=new Stage();pickerStage.setScene(pickerScene);pickerStage.show();picker.applyCss();picker.layout();
            SnapshotWriter.write(pickerScene.snapshot(null),"build/javafx-picker.ppm");pickerStage.close();
            int count=document.items().size();
            smokeMouse(MouseEvent.MOUSE_PRESSED,350,200);smokeMouse(MouseEvent.MOUSE_DRAGGED,450,300);smokeMouse(MouseEvent.MOUSE_RELEASED,450,300);
            if(document.items().size()!=count+1)throw new AssertionError("Mouse drawing failed");
            actions.get("Undo").fire();if(document.items().size()!=count)throw new AssertionError("Undo control failed");
            actions.get("Redo").fire();if(document.items().size()!=count+1)throw new AssertionError("Redo control failed");
            mode=Mode.SELECT;smokeMouse(MouseEvent.MOUSE_PRESSED,400,250);smokeMouse(MouseEvent.MOUSE_RELEASED,400,250);
            if(document.selection().size()!=1)throw new AssertionError("Mouse selection failed");
            double oldX=document.selected().get(0).x1();mode=Mode.MOVE;
            smokeMouse(MouseEvent.MOUSE_PRESSED,400,250);smokeMouse(MouseEvent.MOUSE_DRAGGED,420,280);smokeMouse(MouseEvent.MOUSE_RELEASED,420,280);
            if(document.selected().get(0).x1()!=oldX+20)throw new AssertionError("Mouse movement failed");
            actions.get("Undo").fire();if(document.selected().get(0).x1()!=oldX)throw new AssertionError("Gesture undo failed");
            System.out.println("JavaFX UI smoke test passed");stage.close();Platform.exit();
        }catch(Throwable error){error.printStackTrace();System.exit(1);}
    }
    public static void main(String[] args){launch(args);}
}
