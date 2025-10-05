// src/main/java/com/project/posgunstore/Alerts/DTO/SubscriptionResponse.java
package com.project.posgunstore.Alerts.DTO;

import com.project.posgunstore.Alerts.Model.AlertType;
import com.project.posgunstore.Alerts.Model.SubscriptionType;

import java.time.Instant;

public record SubscriptionResponse(
  Long id,
  AlertType alertType,
  SubscriptionType subscriptionType,
  String target,
  Boolean active,
  Instant createdAt
) {}
