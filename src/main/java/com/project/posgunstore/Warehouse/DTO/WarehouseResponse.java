// WarehouseResponse.java
package com.project.posgunstore.Warehouse.DTO;

import java.time.Instant;

public record WarehouseResponse(
  Long id,
  String name,
  String code,
  String address,
  Boolean active,
  Long version,
  Instant createdAt,
  Instant updatedAt
) {}
