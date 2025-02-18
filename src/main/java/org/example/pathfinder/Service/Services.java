package org.example.pathfinder.Service;

import java.util.List;

public interface Services<T> {
    public void add(T t);
    public void update(T t);

    public void delete(long a);

    public List<T> getall();

    public T getone();
}
