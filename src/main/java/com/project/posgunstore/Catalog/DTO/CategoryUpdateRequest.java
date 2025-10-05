// CategoryUpdateRequest.java
package com.project.posgunstore.Catalog.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryUpdateRequest(
  @NotBlank String name,
  @NotNull Long version
) {}
