package org.example.pathfinder.Service;

import java.util.List;

public interface Services<T> {
    public void add(T t);
    public void update(T t);

    public void delete(T t);

    public List<T> getall();

    public T getone();
}
