// WarehouseUpdateRequest.java
package com.project.posgunstore.Warehouse.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WarehouseUpdateRequest(
  @NotBlank String name,
  @NotBlank String code,
  String address,
  @NotNull Boolean active,
  @NotNull Long version
) {}
