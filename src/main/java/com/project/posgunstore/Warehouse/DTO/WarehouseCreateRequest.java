package com.project.posgunstore.Warehouse.DTO;

import jakarta.validation.constraints.NotBlank;

public record WarehouseCreateRequest(
  @NotBlank String name,
  @NotBlank String code,
  String address,
  Boolean active
) {}
