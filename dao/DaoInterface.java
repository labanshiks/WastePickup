package dao;

import java.util.List;

public interface DaoInterface<T> {

    // Create - save new object to database
    boolean create(T object);

    // Read - get one object by id
    T findById(int id);

    // Read - get all objects
    List<T> findAll();

    // Update - modify existing object
    boolean update(T object);

    // Delete - remove object by id
    boolean delete(int id);
}