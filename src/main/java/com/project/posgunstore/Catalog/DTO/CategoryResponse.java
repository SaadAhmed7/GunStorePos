// CategoryResponse.java
package com.project.posgunstore.Catalog.DTO;

import java.time.Instant;

public record CategoryResponse(
  Long id,
  String name,
  Long version,
  Instant createdAt,
  Instant updatedAt
) {}
