package com.project.posgunstore.Warehouse.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "warehouses",
       indexes = @Index(name = "uq_warehouses_code", columnList = "code", unique = true))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Warehouse {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false) private String name;
  @Column(nullable = false, unique = true, length = 32) private String code;
  private String address;
  @Column(nullable = false) private Boolean active = true;

  @Version private Long version;
  @Column(nullable = false, updatable = false) private Instant createdAt;
  @Column(nullable = false) private Instant updatedAt;

  @PrePersist void onCreate(){ createdAt = updatedAt = Instant.now(); }
  @PreUpdate  void onUpdate(){ updatedAt = Instant.now(); }
}
