package com.project.posgunstore.Catalog.DTO;

import java.math.BigDecimal;

public record ProductListItem(
  Long id,
  String sku,
  String name,
  String categoryName,
  BigDecimal price,
  Boolean isSerialized,
  Boolean isActive,
  String imageUrl
) {}
