// src/main/java/com/project/posgunstore/Reports/Controller/ReportsController.java
package com.project.posgunstore.Reports.Controller;

import com.project.posgunstore.Reports.DTO.*;
import com.project.posgunstore.Reports.Service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {

  private final ReportsService service;

  // GET /api/reports/inventory-valuation
  @GetMapping("/inventory-valuation")
  public InventoryValuationResponse inventoryValuation() {
    return service.inventoryValuation();
  }

  // GET /api/reports/stock-levels
  @GetMapping("/stock-levels")
  public Page<StockLevelRow> stockLevels(@RequestParam(required = false) Long productId,
                                         @RequestParam(required = false) Long warehouseId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "100") int size) {
    return service.stockLevels(productId, warehouseId, page, size);
  }

  // GET /api/reports/stock-movements
  @GetMapping("/stock-movements")
  public Page<StockMovementRow> stockMovements(
      @RequestParam(required = false) Long productId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "100") int size) {
    return service.stockMovements(productId, from, to, page, size);
  }

  // GET /api/reports/serials-status
  @GetMapping("/serials-status")
  public List<SerialsStatusRow> serialsStatus() {
    return service.serialsStatus();
  }
}
