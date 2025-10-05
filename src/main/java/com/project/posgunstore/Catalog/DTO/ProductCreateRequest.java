package com.project.posgunstore.Catalog.DTO;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductCreateRequest(
  @NotBlank String sku,
  @NotBlank String name,
  String description,
  String barcode,
  Long categoryId,
  Long manufacturerId,
  @PositiveOrZero BigDecimal cost,
  @PositiveOrZero BigDecimal price,
  Boolean isSerialized,
  String imageUrl
) {}
