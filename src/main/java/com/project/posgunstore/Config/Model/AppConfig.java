// src/main/java/com/project/posgunstore/Config/Model/AppConfig.java
package com.project.posgunstore.Config.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "app_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppConfig {
  @Id
  @Column(length = 64)
  private String key;          // e.g., "tax", "inventory"

  @Lob
  @Column(nullable = false)
  @JdbcTypeCode(SqlTypes.JSON)
  private String valueJson;    // serialized JSON

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist void onCreate(){ createdAt = updatedAt = Instant.now(); }
  @PreUpdate  void onUpdate(){ updatedAt = Instant.now(); }
}
