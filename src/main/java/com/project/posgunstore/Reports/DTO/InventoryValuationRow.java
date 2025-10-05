// InventoryValuationRow.java
package com.project.posgunstore.Reports.DTO;

import java.math.BigDecimal;

public interface InventoryValuationRow {
  Long getProductId();
  String getSku();
  String getName();
  Integer getQuantity();     // summed
  BigDecimal getCost();
  BigDecimal getValue();     // quantity * cost
}
