// InventoryAdjustment.java
package com.project.posgunstore.Inventory.Model;

import com.project.posgunstore.Catalog.Model.Product;
import com.project.posgunstore.Warehouse.Model.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity @Table(name = "inventory_adjustments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryAdjustment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false) private Product product;
  @ManyToOne(fetch = FetchType.LAZY, optional = false) private Warehouse warehouse;

  @Column(nullable = false) private Integer delta;  // +/- units
  private String reason;

  @Column(nullable = false) private Instant createdAt;

  @PrePersist void onCreate(){ createdAt = Instant.now(); }
}
