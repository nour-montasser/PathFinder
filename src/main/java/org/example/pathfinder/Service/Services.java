package org.example.pathfinder.Service;

import java.util.List;

public interface Services<T> {
    // Create or add an entity
    void add(T entity);

    // Update an existing entity
    void update(T entity);

    // Delete an entity by its ID
    void delete(int id);

    // Retrieve an entity by its ID
    T getById(int id);

    // Retrieve all entities
    List<T> getAll();
}
