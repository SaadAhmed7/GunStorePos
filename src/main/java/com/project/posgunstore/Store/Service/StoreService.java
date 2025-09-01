package com.project.posgunstore.Store.Service;

import com.project.posgunstore.Store.Model.Store;

import java.util.List;
import java.util.UUID;

public interface StoreService {
    List<Store> getAll();
    Store getById(UUID id);
    Store create(Store store);
    Store update(UUID id, Store store);
    void delete(UUID id);
}
