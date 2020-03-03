package model.interfaces;

public interface IObservable {
    void addObserver(IObserver observer);

    void removeObserver(IObserver observer);

    void notifyUpdate();

    void clear();
}
