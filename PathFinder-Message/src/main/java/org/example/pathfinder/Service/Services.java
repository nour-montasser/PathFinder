package org.example.pathfinder.Service;

import javafx.scene.control.ListView;

import java.util.List;

public interface Services<T> {

    public void add(T t);
    public void update(T t);

    void delete(long id); // Delete & update UI

    public List<T> getall();

    public T getone();
}
