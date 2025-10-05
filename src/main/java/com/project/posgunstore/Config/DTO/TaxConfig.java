// src/main/java/com/project/posgunstore/Config/DTO/TaxConfig.java
package com.project.posgunstore.Config.DTO;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TaxConfig(
  @NotNull @DecimalMin("0.00") @DecimalMax("1.00") BigDecimal rate, // 0.07 => 7%
  @NotNull Boolean taxInclusive // true if prices include tax
) {}
