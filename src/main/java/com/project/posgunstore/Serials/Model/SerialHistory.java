// src/main/java/com/project/posgunstore/Serials/Model/SerialHistory.java
package com.project.posgunstore.Serials.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "serial_history", indexes = @Index(name="idx_serial_history_serial", columnList = "serial_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SerialHistory {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Serial serial;

  @Column(nullable = false)
  private Instant at;

  @Column(nullable = false, length = 64)
  private String action; // STATUS_CHANGE | MOVED | COMPLIANCE_EVENT

  @Column(columnDefinition = "text")
  private String details; // JSON/text payload
}
