// StockLevelRow.java
package com.project.posgunstore.Reports.DTO;

public interface StockLevelRow {
  Long getProductId();
  String getSku();
  String getProductName();
  Long getWarehouseId();
  String getWarehouseCode();
  Integer getQuantity();
  Integer getReorderPoint();
}
