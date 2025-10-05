package com.project.posgunstore.Catalog.Model;

import com.project.posgunstore.Catalog.Model.Category;
import com.project.posgunstore.Catalog.Model.Manufacturer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products",
       indexes = {
         @Index(name="idx_products_name", columnList = "name"),
         @Index(name="idx_products_sku", columnList = "sku"),
         @Index(name="idx_products_barcode", columnList = "barcode")
       })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Product {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 64)
  private String sku;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(columnDefinition = "text")
  private String description;

  @Column(unique = true)
  private String barcode;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "manufacturer_id")
  private Manufacturer manufacturer;

  @Column(precision = 12, scale = 2, nullable = false)
  private BigDecimal cost = BigDecimal.ZERO;

  @Column(precision = 12, scale = 2, nullable = false)
  private BigDecimal price = BigDecimal.ZERO;

  @Column(nullable = false)
  private Boolean isSerialized = false;

  @Column(nullable = false)
  private Boolean isActive = true;

  private String imageUrl;

  @Version
  private Long version;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  void onCreate() { createdAt = updatedAt = Instant.now(); }

  @PreUpdate
  void onUpdate() { updatedAt = Instant.now(); }
}
