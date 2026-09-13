import jpaint.model.*;
import java.util.*;

/** Pure model tests run without JavaFX native libraries or a display. */
public final class RegressionTest {
    static int checks;
    static void check(boolean ok,String message){checks++;if(!ok)throw new AssertionError(message);}
    static Artwork shape(double x){return Artwork.shape(Artwork.Kind.RECTANGLE,x,10,x+40,50,"#674EE6","#202330",Artwork.Shading.FILL);}
    public static void main(String[] args){
        Document d=new Document();Artwork a=shape(10),b=shape(100),c=shape(200);d.add(a);d.add(b);d.add(c);
        d.select(Set.of(a.id()));d.move(30,20);d.select(Set.of(b.id()));d.undo();
        check(d.items().get(0).equals(a),"undo restores original moved object");check(d.items().get(1).equals(b),"other selection not moved");
        d.redo();check(d.items().get(0).x1()==40,"redo restores gesture");
        d.select(Set.of(a.id(),b.id()));d.group();check(d.items().size()==2,"group replaces children");
        Artwork group=d.items().get(0);check(group.children().size()==2,"group contents");
        d.copy();d.paste();check(d.items().size()==3,"group paste once");Artwork pasted=d.items().get(2);
        check(!pasted.id().equals(group.id()),"independent group identity");check(!pasted.children().get(0).id().equals(group.children().get(0).id()),"independent child identity");
        check(pasted.children().get(0).primary().equals("#674EE6"),"custom color retained");
        d.undo();check(d.items().size()==2,"undo group paste");d.redo();check(d.items().size()==3,"redo group paste");
        d.paste();check(d.items().get(3).bounds().left()==group.bounds().left()+48,"successive paste offset");
        d.select(Set.of(group.id(),c.id()));d.group();Artwork nested=d.items().get(0);
        check(nested.children().get(0).kind()==Artwork.Kind.GROUP,"nested group retained");
        d.ungroup();check(d.items().get(0).equals(group),"ungroup one level");d.undo();check(d.items().get(0).equals(nested),"undo nested ungroup");
        List<Artwork> before=List.copyOf(d.items());d.delete();d.undo();check(d.items().equals(before),"delete undo preserves stacking");
        d.undo();check(d.canRedo(),"redo available");d.select(Set.of());d.delete();d.move(0,0);d.group();d.ungroup();check(d.canRedo(),"empty commands preserve redo");
        d.add(shape(500));check(!d.canRedo(),"new edit clears redo");
        Document hit=new Document();Artwork ellipse=Artwork.shape(Artwork.Kind.ELLIPSE,100,100,0,0,"#000000","#FFFFFF",Artwork.Shading.OUTLINE);hit.add(ellipse);
        hit.selectAt(2,2);check(hit.selection().isEmpty(),"ellipse corner is not inside shape");hit.selectAt(50,50);check(hit.selection().contains(ellipse.id()),"ellipse interior selected");
        hit.selectRegion(120,120,40,40);check(hit.selection().contains(ellipse.id()),"reverse selection rectangle");
        Artwork triangle=Artwork.shape(Artwork.Kind.TRIANGLE,100,100,0,0,"#000000","#FFFFFF",Artwork.Shading.FILL);
        check(triangle.contains(80,40),"reverse triangle inside");check(!triangle.contains(20,80),"reverse triangle outside");
        List<Artwork> clipboardBefore=hit.items();hit.copy();hit.selectAll();hit.move(10,10);hit.paste();check(hit.items().get(1).bounds().left()==24,"clipboard snapshot independent of later movement");
        int count=hit.items().size();hit.add(Artwork.shape(Artwork.Kind.RECTANGLE,0,0,0,20,"#000000","#FFFFFF",Artwork.Shading.FILL));check(hit.items().size()==count,"zero width draw ignored");
        System.out.println("Passed "+checks+" JavaFX rewrite model checks.");
    }
}
