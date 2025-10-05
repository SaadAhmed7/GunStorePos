// src/main/java/com/project/posgunstore/Serials/DTO/SerialUpdateRequest.java
package com.project.posgunstore.Serials.DTO;

import com.project.posgunstore.Serials.Model.SerialStatus;
import jakarta.validation.constraints.NotNull;

public record SerialUpdateRequest(
  @NotNull SerialStatus status,
  Long warehouseId,   // optional: move while updating status (e.g., AVAILABLE move)
  @NotNull Long version
) {}
