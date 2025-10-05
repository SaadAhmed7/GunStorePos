// InventoryMovement.java
package com.project.posgunstore.Inventory.Model;

import com.project.posgunstore.Catalog.Model.Product;
import com.project.posgunstore.Warehouse.Model.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity @Table(name = "inventory_movements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryMovement {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false) private Product product;
  @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name="from_wh_id")
  private Warehouse fromWarehouse;
  @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name="to_wh_id")
  private Warehouse toWarehouse;

  @Column(nullable = false) private Integer quantity; // positive
  @Column(nullable = false) private Instant createdAt;

  @PrePersist void onCreate(){ createdAt = Instant.now(); }
}
