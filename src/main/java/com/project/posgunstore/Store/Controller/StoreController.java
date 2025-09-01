package com.project.posgunstore.Store.Controller;

import com.project.posgunstore.Store.Model.Store;
import com.project.posgunstore.Store.Service.StoreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public List<Store> getAll() {
        return storeService.getAll();
    }

    @GetMapping("/{id}")
    public Store getById(@PathVariable UUID id) {
        return storeService.getById(id);
    }

    @PostMapping
    public Store create(@RequestBody Store store) {
        return storeService.create(store);
    }

    @PutMapping("/{id}")
    public Store update(@PathVariable UUID id, @RequestBody Store store) {
        return storeService.update(id, store);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        storeService.delete(id);
    }
}
