package model;

import model.interfaces.IObservable;
import model.interfaces.IObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Make observable
 */
public class PaintObservable implements IObservable {

    List<IObserver> observerList = new ArrayList<IObserver>();

    public PaintObservable() {
    }

    @Override
    public void addObserver(IObserver observer) {
        if (!observerList.contains(observer)) {
            observerList.add(observer);
            System.out.println("add called from PaintObservable");
        }
    }

    @Override
    public void removeObserver(IObserver observer) {
        observerList.remove(observer);

        System.out.println("remove called from PaintObservable");
    }

    @Override
    public void notifyUpdate() {
        for (IObserver observer : observerList) {
            observer.update();
        }
    }

    @Override
    public void clear() {
        observerList.clear();
        System.out.println("clear called from PaintObservable");
    }
}
