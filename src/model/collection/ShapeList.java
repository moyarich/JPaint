package model.collection;

import model.interfaces.IShape;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks the shapes on the screen
 *
 * @author Moya Richards
 */
public class ShapeList {
    private final List<IShape> shapeList = new ArrayList<>();
    private String listLabel;

    public ShapeList(String listLabel) {
        this.listLabel = listLabel;
    }

    public void add(IShape item) {

        if (!shapeList.contains(item)) {
            shapeList.add(item);
            System.out.println("add called from " + listLabel + " collection");
        }
    }

    public IShape get(int index) {
        System.out.println("get called from " + listLabel + " collection");

        return shapeList.get(index);
    }

    public void remove(IShape item) {
        shapeList.remove(item);

        System.out.println("remove called from " + listLabel + " collection");
    }

    public boolean contains(IShape item) {
        System.out.println("contain called from " + listLabel + " collection");

        return shapeList.contains(item);
    }

    public void clear() {
        shapeList.clear();
        System.out.println("clear called from " + listLabel + " collection");
    }

    public int size() {
        return shapeList.size();
    }

    public List<IShape> getList() {
        return shapeList;
    }

    public void addAll(List<IShape> list) {
        shapeList.addAll(list);
    }

    public void removeAll(List<IShape> list) {
        shapeList.removeAll(list);
    }


    @Override
    public String toString() {
        return "ShapeList{" +
                "shapeList=" + shapeList +
                ", listLabel='" + listLabel + '\'' +
                '}';
    }
}