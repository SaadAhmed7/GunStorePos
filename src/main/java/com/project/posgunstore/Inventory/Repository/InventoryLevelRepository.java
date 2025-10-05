// InventoryLevelRepository.java
package com.project.posgunstore.Inventory.Repository;

import com.project.posgunstore.Inventory.Model.InventoryLevel;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryLevelRepository extends JpaRepository<InventoryLevel, Long> {

  Optional<InventoryLevel> findByProduct_IdAndWarehouse_Id(Long productId, Long warehouseId);

  @Query("""
     select il from InventoryLevel il
     where (:threshold is not null and il.quantity <= :threshold)
        or (:threshold is null and il.quantity <= il.reorderPoint)
     """)
  Page<InventoryLevel> lowStock(@Param("threshold") Integer threshold, Pageable pageable);

  long countByWarehouse_Id(Long warehouseId);

  @EntityGraph(attributePaths = {"product", "warehouse"})
  List<InventoryLevel> findAll();
}
