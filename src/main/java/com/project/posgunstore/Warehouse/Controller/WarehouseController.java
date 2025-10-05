// WarehouseController.java
package com.project.posgunstore.Warehouse.Controller;

import com.project.posgunstore.Warehouse.DTO.*;
import com.project.posgunstore.Warehouse.Service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

  private final WarehouseService service;

  @PostMapping
  public ResponseEntity<WarehouseResponse> create(@Valid @RequestBody WarehouseCreateRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
  }

  @GetMapping
  public Page<WarehouseResponse> list(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "50") int size) {
    return service.list(page, size);
  }

  @GetMapping("/{id}")
  public WarehouseResponse get(@PathVariable Long id) { return service.get(id); }

  @PutMapping("/{id}")
  public WarehouseResponse update(@PathVariable Long id, @Valid @RequestBody WarehouseUpdateRequest req) {
    return service.update(id, req);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) { service.delete(id); }
}
