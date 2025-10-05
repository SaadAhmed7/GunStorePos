// InventoryAdjustmentRepository.java
package com.project.posgunstore.Inventory.Repository;

import com.project.posgunstore.Inventory.Model.InventoryAdjustment;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface InventoryAdjustmentRepository extends JpaRepository<InventoryAdjustment, Long> {

  @Query("""
     select a from InventoryAdjustment a
     where (:productId is null or a.product.id = :productId)
       and (:warehouseId is null or a.warehouse.id = :warehouseId)
       and (:fromTs is null or a.createdAt >= :fromTs)
       and (:toTs is null or a.createdAt < :toTs)
     """)
  Page<InventoryAdjustment> search(@Param("productId") Long productId,
                                   @Param("warehouseId") Long warehouseId,
                                   @Param("fromTs") Instant from,
                                   @Param("toTs") Instant to,
                                   Pageable pageable);
}
