// InventoryMovementRepository.java
package com.project.posgunstore.Inventory.Repository;

import com.project.posgunstore.Inventory.Model.InventoryMovement;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

  @Query("""
     select m from InventoryMovement m
     where (:productId is null or m.product.id = :productId)
       and (:fromWh is null or m.fromWarehouse.id = :fromWh)
       and (:toWh is null or m.toWarehouse.id = :toWh)
       and (:fromTs is null or m.createdAt >= :fromTs)
       and (:toTs is null or m.createdAt < :toTs)
     """)
  Page<InventoryMovement> search(@Param("productId") Long productId,
                                 @Param("fromWh") Long fromWarehouseId,
                                 @Param("toWh") Long toWarehouseId,
                                 @Param("fromTs") Instant from,
                                 @Param("toTs") Instant to,
                                 Pageable pageable);
}
