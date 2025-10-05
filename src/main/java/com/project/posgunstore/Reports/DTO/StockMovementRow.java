// StockMovementRow.java
package com.project.posgunstore.Reports.DTO;

import java.time.Instant;

public interface StockMovementRow {
  String getType();              // ADJUSTMENT | TRANSFER
  Long getProductId();
  String getSku();
  String getProductName();
  String getFromWarehouseCode(); // null for adjustments
  String getToWarehouseCode();   // null for adjustments
  String getWarehouseCode();     // only for adjustments
  Integer getQuantity();         // +/- for adjustments; + for transfers
  String getReason();            // adjustments only
  Instant getCreatedAt();
}
