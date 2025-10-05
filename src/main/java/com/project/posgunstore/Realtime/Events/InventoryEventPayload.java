// src/main/java/com/project/posgunstore/Realtime/Events/InventoryEventPayload.java
package com.project.posgunstore.Realtime.Events;

import java.time.Instant;

public record InventoryEventPayload(
  Long productId,
  Long warehouseId,
  Integer quantity,
  Integer reorderPoint,
  Instant at
) {}
