// InventoryMovementRequest.java
package com.project.posgunstore.Inventory.DTO;

import jakarta.validation.constraints.NotNull;

public record InventoryMovementRequest(
  @NotNull Long productId,
  @NotNull Long fromWarehouseId,
  @NotNull Long toWarehouseId,
  @NotNull Integer quantity
) {}
