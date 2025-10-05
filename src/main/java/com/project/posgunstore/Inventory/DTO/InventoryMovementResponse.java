// InventoryMovementResponse.java
package com.project.posgunstore.Inventory.DTO;

import java.time.Instant;

public record InventoryMovementResponse(
  Long id,
  Long productId,
  Long fromWarehouseId,
  Long toWarehouseId,
  Integer quantity,
  Instant createdAt
) {}
