// ManufacturerResponse.java
package com.project.posgunstore.Catalog.DTO;

import java.time.Instant;

public record ManufacturerResponse(
  Long id,
  String name,
  Long version,
  Instant createdAt,
  Instant updatedAt
) {}
