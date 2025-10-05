// WarehouseRepository.java
package com.project.posgunstore.Warehouse.Repository;

import com.project.posgunstore.Warehouse.Model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
  boolean existsByCodeIgnoreCase(String code);
  Optional<Warehouse> findByCodeIgnoreCase(String code);
}
