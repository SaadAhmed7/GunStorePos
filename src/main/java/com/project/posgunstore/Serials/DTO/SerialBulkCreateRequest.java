// src/main/java/com/project/posgunstore/Serials/DTO/SerialBulkCreateRequest.java
package com.project.posgunstore.Serials.DTO;

import jakarta.validation.constraints.*;
import java.util.List;

public record SerialBulkCreateRequest(
  @NotNull Long warehouseId,
  @NotEmpty List<@NotBlank String> serialNumbers
) {}
