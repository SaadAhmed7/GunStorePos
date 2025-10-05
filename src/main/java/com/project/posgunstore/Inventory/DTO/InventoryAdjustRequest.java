// InventoryAdjustRequest.java
package com.project.posgunstore.Inventory.DTO;

import jakarta.validation.constraints.NotNull;

public record InventoryAdjustRequest(
  @NotNull Long productId,
  @NotNull Long warehouseId,
  @NotNull Integer delta,
  String reason
) {}
