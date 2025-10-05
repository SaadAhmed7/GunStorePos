// InventoryValuationResponse.java
package com.project.posgunstore.Reports.DTO;

import java.math.BigDecimal;
import java.util.List;

public record InventoryValuationResponse(
  List<InventoryValuationRow> rows,
  BigDecimal totalValue
) {}
