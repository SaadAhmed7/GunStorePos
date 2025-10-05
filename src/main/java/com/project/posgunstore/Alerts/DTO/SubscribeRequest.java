// src/main/java/com/project/posgunstore/Alerts/DTO/SubscribeRequest.java
package com.project.posgunstore.Alerts.DTO;

import com.project.posgunstore.Alerts.Model.AlertType;
import com.project.posgunstore.Alerts.Model.SubscriptionType;
import jakarta.validation.constraints.*;

public record SubscribeRequest(
  @NotNull AlertType alertType,
  @NotNull SubscriptionType subscriptionType,
  @NotBlank String target
) {}
