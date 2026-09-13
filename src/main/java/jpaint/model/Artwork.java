package jpaint.model;

import java.util.*;

/** Immutable artwork; groups retain their children and shape orientation. */
public record Artwork(String id, Kind kind, double x1, double y1, double x2, double y2,
                      String primary, String secondary, Shading shading, List<Artwork> children) {
    public enum Kind { RECTANGLE, ELLIPSE, TRIANGLE, GROUP }
    public enum Shading { FILL, OUTLINE, FILL_AND_OUTLINE }
    public Artwork { children = List.copyOf(children); }
    public static Artwork shape(Kind kind, double x1, double y1, double x2, double y2,
                                String primary, String secondary, Shading shading) {
        return new Artwork(UUID.randomUUID().toString(), kind, x1, y1, x2, y2, primary, secondary, shading, List.of());
    }
    public static Artwork group(List<Artwork> children) {
        return new Artwork(UUID.randomUUID().toString(), Kind.GROUP,0,0,0,0,"#000000","#000000",Shading.FILL,children);
    }
    public Artwork translate(double dx, double dy) {
        return new Artwork(id,kind,x1+dx,y1+dy,x2+dx,y2+dy,primary,secondary,shading,
                children.stream().map(c -> c.translate(dx,dy)).toList());
    }
    public Artwork duplicate() {
        return new Artwork(UUID.randomUUID().toString(),kind,x1,y1,x2,y2,primary,secondary,shading,
                children.stream().map(Artwork::duplicate).toList());
    }
    public Bounds bounds() {
        if (kind != Kind.GROUP) return new Bounds(Math.min(x1,x2),Math.min(y1,y2),Math.max(x1,x2),Math.max(y1,y2));
        return children.stream().map(Artwork::bounds).reduce(Bounds::union).orElse(new Bounds(0,0,0,0));
    }
    public boolean contains(double x, double y) {
        if (kind == Kind.GROUP) return children.stream().anyMatch(c -> c.contains(x,y));
        Bounds b=bounds();
        if (!b.contains(x,y)) return false;
        if (kind == Kind.RECTANGLE) return true;
        if (kind == Kind.ELLIPSE) {
            if (b.width()==0 || b.height()==0) return false;
            double dx=(x-(b.left+b.right)/2)/(b.width()/2),dy=(y-(b.top+b.bottom)/2)/(b.height()/2);
            return dx*dx+dy*dy<=1;
        }
        if (x1==x2 || y1==y2) return false;
        double u=(x-x1)/(x2-x1),v=(y-y1)/(y2-y1);
        return u>=0 && u<=1 && v>=0 && v<=1 && u<=v;
    }
    public record Bounds(double left,double top,double right,double bottom) {
        public double width(){return right-left;} public double height(){return bottom-top;}
        public boolean contains(double x,double y){return x>=left && x<=right && y>=top && y<=bottom;}
        public boolean intersects(Bounds b){return left<=b.right && right>=b.left && top<=b.bottom && bottom>=b.top;}
        public Bounds union(Bounds b){return new Bounds(Math.min(left,b.left),Math.min(top,b.top),Math.max(right,b.right),Math.max(bottom,b.bottom));}
    }
}
