package model.interfaces;

public interface ICollection<T> {
    T get(int index);

    void remove(T item);

    boolean contains(T item);

    void clear();

}