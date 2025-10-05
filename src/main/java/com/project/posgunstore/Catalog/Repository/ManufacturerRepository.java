package com.project.posgunstore.Catalog.Repository;

import com.project.posgunstore.Catalog.Model.Manufacturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
    boolean existsByNameIgnoreCase(String name);
    Page<Manufacturer> findByNameContainingIgnoreCase(String name, Pageable pageable);
}