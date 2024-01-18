package com.toy.barterx.firebasehelper;

import java.util.Map;

public interface ICrud {
    public <T> void add(T data);
    public <T> T getById(String id);
    public <T> Map<T,T> findAll();
    public <T> boolean update(String id,T data);
    public boolean remove(String id);
}
