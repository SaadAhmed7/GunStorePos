// src/main/java/com/project/posgunstore/Serials/DTO/ComplianceEventRequest.java
package com.project.posgunstore.Serials.DTO;

import jakarta.validation.constraints.NotBlank;

public record ComplianceEventRequest(
  @NotBlank String type,
  String payload
) {}
