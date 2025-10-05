// src/main/java/com/project/posgunstore/Serials/Model/Serial.java
package com.project.posgunstore.Serials.Model;

import com.project.posgunstore.Catalog.Model.Product;
import com.project.posgunstore.Warehouse.Model.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "serials", indexes = {
    @Index(name = "uq_serials_serial_number", columnList = "serialNumber", unique = true),
    @Index(name = "idx_serials_product", columnList = "product_id"),
    @Index(name = "idx_serials_warehouse", columnList = "warehouse_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Serial {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Product product;

  @Column(nullable = false, unique = true, length = 128)
  private String serialNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SerialStatus status = SerialStatus.AVAILABLE;

  @ManyToOne(fetch = FetchType.LAZY)
  private Warehouse warehouse; // Nullable when SOLD (if you prefer), or keep last location

  @Version
  private Long version;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist void onCreate(){ createdAt = updatedAt = Instant.now(); }
  @PreUpdate  void onUpdate(){ updatedAt = Instant.now(); }
}
