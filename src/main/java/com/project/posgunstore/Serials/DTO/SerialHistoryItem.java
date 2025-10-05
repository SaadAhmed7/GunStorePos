// src/main/java/com/project/posgunstore/Serials/DTO/SerialHistoryItem.java
package com.project.posgunstore.Serials.DTO;

import java.time.Instant;

public record SerialHistoryItem(
  Long id,
  Instant at,
  String action,
  String details
) {}
