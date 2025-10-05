// InventoryAdjustmentResponse.java
package com.project.posgunstore.Inventory.DTO;

import java.time.Instant;

public record InventoryAdjustmentResponse(
  Long id,
  Long productId,
  Long warehouseId,
  Integer delta,
  String reason,
  Instant createdAt
) {}
