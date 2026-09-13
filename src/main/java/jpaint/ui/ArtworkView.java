package jpaint.ui;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import jpaint.model.Artwork;

public final class ArtworkView {
    private ArtworkView(){}
    public static Node create(Artwork a) {
        if(a.kind()==Artwork.Kind.GROUP){Group group=new Group();a.children().forEach(c -> group.getChildren().add(create(c)));return group;}
        Artwork.Bounds b=a.bounds();
        Shape shape=switch(a.kind()){
            case RECTANGLE -> new Rectangle(b.left(),b.top(),b.width(),b.height());
            case ELLIPSE -> new Ellipse((b.left()+b.right())/2,(b.top()+b.bottom())/2,b.width()/2,b.height()/2);
            case TRIANGLE -> new Polygon(a.x1(),a.y1(),a.x1(),a.y2(),a.x2(),a.y2());
            default -> throw new IllegalArgumentException("Unexpected shape");
        };
        shape.setFill(a.shading()==Artwork.Shading.OUTLINE?Color.TRANSPARENT:Color.web(a.primary()));
        shape.setStroke(a.shading()==Artwork.Shading.FILL?null:Color.web(a.shading()==Artwork.Shading.OUTLINE?a.primary():a.secondary()));
        shape.setStrokeWidth(3);shape.setStrokeLineJoin(StrokeLineJoin.ROUND);
        return shape;
    }
}
