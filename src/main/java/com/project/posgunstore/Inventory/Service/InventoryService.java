// InventoryService.java
package com.project.posgunstore.Inventory.Service;

import com.project.posgunstore.Inventory.DTO.*;
import org.springframework.data.domain.Page;

import java.time.Instant;

public interface InventoryService {
  InventoryLevelResponse getLevel(Long productId, Long warehouseId);
  InventoryAdjustmentResponse adjust(InventoryAdjustRequest req);
  InventoryMovementResponse transfer(InventoryMovementRequest req);

  Page<InventoryAdjustmentResponse> listAdjustments(Long productId, Long warehouseId,
                                                    Instant from, Instant to,
                                                    int page, int size);
  Page<InventoryMovementResponse> listMovements(Long productId, Long fromWarehouseId, Long toWarehouseId,
                                                Instant from, Instant to,
                                                int page, int size);

  Page<InventoryLevelResponse> lowStock(Integer threshold, int page, int size);
}
