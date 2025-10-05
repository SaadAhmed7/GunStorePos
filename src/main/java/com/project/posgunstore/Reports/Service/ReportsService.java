// src/main/java/com/project/posgunstore/Reports/Service/ReportsService.java
package com.project.posgunstore.Reports.Service;

import com.project.posgunstore.Reports.DTO.*;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;

public interface ReportsService {
  InventoryValuationResponse inventoryValuation();
  Page<StockLevelRow> stockLevels(Long productId, Long warehouseId, int page, int size);
  Page<StockMovementRow> stockMovements(Long productId, Instant from, Instant to, int page, int size);
  List<SerialsStatusRow> serialsStatus();
}
