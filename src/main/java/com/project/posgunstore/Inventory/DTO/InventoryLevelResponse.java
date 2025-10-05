// InventoryLevelResponse.java
package com.project.posgunstore.Inventory.DTO;

public record InventoryLevelResponse(
  Long productId,
  Long warehouseId,
  Integer quantity,
  Integer reorderPoint
) {}
