// InventoryController.java
package com.project.posgunstore.Inventory.Controller;

import com.project.posgunstore.Inventory.DTO.*;
import com.project.posgunstore.Inventory.Service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

  private final InventoryService service;

  // GET /api/inventory/levels?productId=&warehouseId=
  @GetMapping("/levels")
  public ResponseEntity<InventoryLevelResponse> level(@RequestParam Long productId,
                                                      @RequestParam Long warehouseId) {
    return ResponseEntity.ok(service.getLevel(productId, warehouseId));
  }

  // POST /api/inventory/adjustments
  @PostMapping("/adjustments")
  public ResponseEntity<InventoryAdjustmentResponse> adjust(@Valid @RequestBody InventoryAdjustRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.adjust(req));
  }

  // GET /api/inventory/adjustments
  @GetMapping("/adjustments")
  public Page<InventoryAdjustmentResponse> listAdjustments(
      @RequestParam(required = false) Long productId,
      @RequestParam(required = false) Long warehouseId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {
    return service.listAdjustments(productId, warehouseId, from, to, page, size);
  }

  // POST /api/inventory/movements
  @PostMapping("/movements")
  public ResponseEntity<InventoryMovementResponse> move(@Valid @RequestBody InventoryMovementRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.transfer(req));
  }

  // GET /api/inventory/movements
  @GetMapping("/movements")
  public Page<InventoryMovementResponse> listMovements(
      @RequestParam(required = false) Long productId,
      @RequestParam(required = false) Long fromWarehouseId,
      @RequestParam(required = false) Long toWarehouseId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {
    return service.listMovements(productId, fromWarehouseId, toWarehouseId, from, to, page, size);
  }

  // GET /api/inventory/low-stock?threshold={n}
  @GetMapping("/low-stock")
  public Page<InventoryLevelResponse> lowStock(@RequestParam(required = false) Integer threshold,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "50") int size) {
    return service.lowStock(threshold, page, size);
  }
}
