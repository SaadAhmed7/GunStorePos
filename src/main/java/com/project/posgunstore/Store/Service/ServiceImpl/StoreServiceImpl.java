package com.project.posgunstore.Store.Service.ServiceImpl;

import com.project.posgunstore.Store.Model.Store;
import com.project.posgunstore.Store.Repository.StoreRepository;
import com.project.posgunstore.Store.Service.StoreService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public List<Store> getAll() {
        return storeRepository.findAll();
    }

    @Override
    public Store getById(UUID id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found with id " + id));
    }

    @Override
    public Store create(Store store) {
        return storeRepository.save(store);
    }

    @Override
    public Store update(UUID id, Store store) {
        Store existing = getById(id);
        existing.setName(store.getName());
        existing.setPhone(store.getPhone());
        existing.setAddress(store.getAddress());
        existing.setCity(store.getCity());
        existing.setStatus(store.getStatus());
        existing.setZipCode(store.getZipCode());
        existing.setEmail(store.getEmail());
        existing.setWebsite(store.getWebsite());
        return storeRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        storeRepository.deleteById(id);
    }
}
