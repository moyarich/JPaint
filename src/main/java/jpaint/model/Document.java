package jpaint.model;

import java.util.*;

/** Snapshot history makes each edit atomic, including nested groups and layer order. */
public final class Document {
    private record State(List<Artwork> items, Set<String> selection) {
        State { items=List.copyOf(items); selection=Set.copyOf(selection); }
    }
    private State state=new State(List.of(),Set.of());
    private final Deque<State> undo=new ArrayDeque<>(),redo=new ArrayDeque<>();
    private List<Artwork> clipboard=List.of();
    private int pasteCount;
    public List<Artwork> items(){return state.items;}
    public Set<String> selection(){return state.selection;}
    public boolean canUndo(){return !undo.isEmpty();} public boolean canRedo(){return !redo.isEmpty();}
    public boolean canPaste(){return !clipboard.isEmpty();}
    public boolean canUngroup(){return selected().stream().anyMatch(a -> a.kind()==Artwork.Kind.GROUP);}
    public List<Artwork> selected(){return items().stream().filter(a -> selection().contains(a.id())).toList();}
    private void commit(List<Artwork> items, Set<String> selection) {
        State next=new State(items,selection);
        if (next.equals(state)) return;
        undo.push(state); redo.clear(); state=next;
    }
    public void add(Artwork artwork) {
        if (artwork.bounds().width()<1 || artwork.bounds().height()<1) return;
        List<Artwork> next=new ArrayList<>(items());next.add(artwork);commit(next,Set.of());
    }
    public void select(Set<String> ids) {
        Set<String> valid=new HashSet<>(ids);valid.retainAll(items().stream().map(Artwork::id).toList());
        state=new State(items(),valid);
    }
    public void selectAt(double x,double y) {
        for(int i=items().size()-1;i>=0;i--) if(items().get(i).contains(x,y)){select(Set.of(items().get(i).id()));return;}
        select(Set.of());
    }
    public void selectRegion(double x1,double y1,double x2,double y2) {
        Artwork.Bounds box=new Artwork.Bounds(Math.min(x1,x2),Math.min(y1,y2),Math.max(x1,x2),Math.max(y1,y2));
        Set<String> selected=new HashSet<>();for(Artwork a:items())if(a.bounds().intersects(box))selected.add(a.id());select(selected);
    }
    public void selectAll(){select(new HashSet<>(items().stream().map(Artwork::id).toList()));}
    public void move(double dx,double dy) {
        if(selection().isEmpty() || (dx==0 && dy==0))return;
        commit(items().stream().map(a -> selection().contains(a.id())?a.translate(dx,dy):a).toList(),selection());
    }
    public void delete(){if(!selection().isEmpty())commit(items().stream().filter(a -> !selection().contains(a.id())).toList(),Set.of());}
    public void copy(){if(selection().isEmpty())return;clipboard=selected().stream().map(Artwork::duplicate).toList();pasteCount=0;}
    public void paste(){
        if(!canPaste())return;
        double offset=24*++pasteCount;
        List<Artwork> pasted=clipboard.stream().map(Artwork::duplicate).map(a -> a.translate(offset,offset)).toList();
        List<Artwork> next=new ArrayList<>(items());next.addAll(pasted);
        commit(next,new HashSet<>(pasted.stream().map(Artwork::id).toList()));
    }
    public void group(){
        if(selection().size()<2)return;
        Artwork group=Artwork.group(selected());List<Artwork> next=new ArrayList<>();
        int last=-1;for(int i=0;i<items().size();i++)if(selection().contains(items().get(i).id()))last=i;
        for(int i=0;i<items().size();i++){
            Artwork a=items().get(i);if(!selection().contains(a.id()))next.add(a);
            if(i==last)next.add(group);
        }
        commit(next,Set.of(group.id()));
    }
    public void ungroup(){
        if(!canUngroup())return;
        List<Artwork> next=new ArrayList<>();Set<String> selected=new HashSet<>();
        for(Artwork a:items()){
            if(selection().contains(a.id()) && a.kind()==Artwork.Kind.GROUP){next.addAll(a.children());for(Artwork c:a.children())selected.add(c.id());}
            else {next.add(a);if(selection().contains(a.id()))selected.add(a.id());}
        }
        commit(next,selected);
    }
    public void undo(){if(canUndo()){redo.push(state);state=undo.pop();}}
    public void redo(){if(canRedo()){undo.push(state);state=redo.pop();}}
}
