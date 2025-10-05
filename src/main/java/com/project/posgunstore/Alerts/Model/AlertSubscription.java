// src/main/java/com/project/posgunstore/Alerts/Model/AlertSubscription.java
package com.project.posgunstore.Alerts.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "alert_subscriptions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlertSubscription {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING) @Column(nullable = false)
  private AlertType alertType;

  @Enumerated(EnumType.STRING) @Column(nullable = false)
  private SubscriptionType subscriptionType; // EMAIL | WEBHOOK

  @Column(nullable = false)
  private String target; // email address or webhook URL

  @Column(nullable = false)
  private Boolean active = true;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist void onCreate(){ createdAt = Instant.now(); }
}
