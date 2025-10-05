// src/main/java/com/project/posgunstore/Serials/Model/ComplianceEvent.java
package com.project.posgunstore.Serials.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "serial_compliance_events", indexes = @Index(name="idx_serial_compliance_serial", columnList = "serial_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComplianceEvent {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Serial serial;

  @Column(nullable = false) private Instant at;
  @Column(nullable = false, length = 64) private String type; // e.g., "4473_CHECK", "WARRANTY"
  @Column(columnDefinition = "text") private String payload;  // JSON/text

  @PrePersist void onCreate(){ if (at == null) at = Instant.now(); }
}
