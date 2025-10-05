// CategoryCreateRequest.java
package com.project.posgunstore.Catalog.DTO;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
  @NotBlank String name
) {}
