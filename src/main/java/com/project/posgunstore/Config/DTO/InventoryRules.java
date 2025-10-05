// src/main/java/com/project/posgunstore/Config/DTO/InventoryRules.java
package com.project.posgunstore.Config.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryRules(
  @NotNull Boolean allowNegativeStock,     // usually false
  @PositiveOrZero Integer defaultReorderPoint, // default per new InventoryLevel
  @PositiveOrZero Integer lowStockThreshold // global fallback if per-item not set
) {}
