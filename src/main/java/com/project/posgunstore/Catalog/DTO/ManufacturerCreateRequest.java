// ManufacturerCreateRequest.java
package com.project.posgunstore.Catalog.DTO;

import jakarta.validation.constraints.NotBlank;

public record ManufacturerCreateRequest(
  @NotBlank String name
) {}
