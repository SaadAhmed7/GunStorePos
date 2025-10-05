// src/main/java/com/project/posgunstore/Alerts/DTO/LowStockItem.java
package com.project.posgunstore.Alerts.DTO;

public record LowStockItem(
  Long productId,
  String sku,
  String productName,
  Long warehouseId,
  String warehouseCode,
  Integer quantity,
  Integer reorderPoint
) {}
