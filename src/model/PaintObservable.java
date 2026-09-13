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

        }
    }

    @Override
    public void removeObserver(IObserver observer) {
        observerList.remove(observer);

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

    }
}
