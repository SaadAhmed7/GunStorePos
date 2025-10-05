// src/main/java/com/project/posgunstore/Storage/DTO/InventoryExportRow.java
package com.project.posgunstore.Storage.DTO;

import java.math.BigDecimal;

public record InventoryExportRow(
  Long productId,
  String sku,
  String productName,
  Long warehouseId,
  String warehouseCode,
  Integer quantity,
  Integer reorderPoint,
  BigDecimal cost,
  BigDecimal extendedValue // quantity * cost
) {}
