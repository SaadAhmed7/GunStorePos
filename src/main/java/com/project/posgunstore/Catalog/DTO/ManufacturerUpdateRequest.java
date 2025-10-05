// ManufacturerUpdateRequest.java
package com.project.posgunstore.Catalog.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ManufacturerUpdateRequest(
  @NotBlank String name,
  @NotNull Long version
) {}
