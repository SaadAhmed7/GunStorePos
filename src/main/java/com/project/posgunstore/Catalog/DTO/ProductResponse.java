package com.project.posgunstore.Catalog.DTO;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
  Long id,
  String sku,
  String name,
  String description,
  String barcode,
  Long categoryId,
  String categoryName,
  Long manufacturerId,
  String manufacturerName,
  BigDecimal cost,
  BigDecimal price,
  Boolean isSerialized,
  Boolean isActive,
  String imageUrl,
  Long version,
  Instant createdAt,
  Instant updatedAt
) {}
