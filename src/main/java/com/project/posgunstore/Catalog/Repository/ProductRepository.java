package com.project.posgunstore.Catalog.Repository;

import com.project.posgunstore.Catalog.Model.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findBySku(String sku);
  Optional<Product> findByBarcode(String barcode);
  boolean existsBySku(String sku);
  boolean existsByBarcode(String barcode);

  @Query("""
         select p from Product p
         where p.isActive = true
           and ( lower(p.name) like lower(concat('%', :q, '%'))
              or lower(p.sku) like lower(concat('%', :q, '%'))
              or lower(p.barcode) like lower(concat('%', :q, '%')) )
         """)
  Page<Product> search(@Param("q") String q, Pageable pageable);

  Page<Product> findAllByIsActiveTrue(Pageable pageable);

  long countByCategory_Id(Long categoryId);

  long countByManufacturer_Id(Long manufacturerId);
}
