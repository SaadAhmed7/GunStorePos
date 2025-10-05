// ManufacturerController.java
package com.project.posgunstore.Catalog.Controller;

import com.project.posgunstore.Catalog.DTO.*;
import com.project.posgunstore.Catalog.Service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

  private final ManufacturerService service;

  @PostMapping
  public ResponseEntity<ManufacturerResponse> create(@Valid @RequestBody ManufacturerCreateRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
  }

  @GetMapping
  public Page<ManufacturerResponse> list(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "50") int size,
                                         @RequestParam(required = false) String q) {
    return service.list(page, size, q);
  }

  @GetMapping("/{id}")
  public ManufacturerResponse get(@PathVariable Long id) { return service.get(id); }

  @PutMapping("/{id}")
  public ManufacturerResponse update(@PathVariable Long id, @Valid @RequestBody ManufacturerUpdateRequest req) {
    return service.update(id, req);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) { service.delete(id); }
}
