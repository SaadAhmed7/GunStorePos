// src/main/java/com/project/posgunstore/Serials/DTO/SerialResponse.java
package com.project.posgunstore.Serials.DTO;

import com.project.posgunstore.Serials.Model.SerialStatus;
import java.time.Instant;

public record SerialResponse(
  Long id,
  Long productId,
  String productSku,
  String productName,
  String serialNumber,
  SerialStatus status,
  Long warehouseId,
  String warehouseCode,
  Long version,
  Instant createdAt,
  Instant updatedAt
) {}
