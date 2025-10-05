// InventoryLevel.java
package com.project.posgunstore.Inventory.Model;

import com.project.posgunstore.Catalog.Model.Product;
import com.project.posgunstore.Warehouse.Model.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "inventory_levels",
       uniqueConstraints = @UniqueConstraint(columnNames = {"product_id","warehouse_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryLevel {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false) private Product product;
  @ManyToOne(fetch = FetchType.LAZY, optional = false) private Warehouse warehouse;

  @Column(nullable = false) private Integer quantity = 0;
  @Column(nullable = false) private Integer reorderPoint = 0;

  @Version private Long version;
  @Column(nullable = false, updatable = false) private Instant createdAt;
  @Column(nullable = false) private Instant updatedAt;
  @PrePersist void onCreate(){ createdAt = updatedAt = Instant.now(); }
  @PreUpdate  void onUpdate(){ updatedAt = Instant.now(); }
}
