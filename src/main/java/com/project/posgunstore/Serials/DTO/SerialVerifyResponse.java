// src/main/java/com/project/posgunstore/Serials/DTO/SerialVerifyResponse.java
package com.project.posgunstore.Serials.DTO;

import com.project.posgunstore.Serials.Model.SerialStatus;

public record SerialVerifyResponse(
  boolean valid,
  Long serialId,
  Long productId,
  String productSku,
  SerialStatus status,
  Long warehouseId
) {}
