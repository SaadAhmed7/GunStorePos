// src/main/java/com/project/posgunstore/Serials/Repository/SerialRepository.java
package com.project.posgunstore.Serials.Repository;

import com.project.posgunstore.Serials.Model.Serial;
import com.project.posgunstore.Serials.Model.SerialStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface SerialRepository extends JpaRepository<Serial, Long> {
  Optional<Serial> findBySerialNumber(String serialNumber);
  boolean existsBySerialNumber(String serialNumber);

  @Query("""
      select s from Serial s
      where (:productId is null or s.product.id = :productId)
        and (:status is null or s.status = :status)
        and (
            :q is null or :q = '' or
            lower(s.serialNumber) like lower(concat('%', :q, '%'))
        )
      """)
  Page<Serial> search(@Param("productId") Long productId,
                      @Param("status") SerialStatus status,
                      @Param("q") String q,
                      Pageable pageable);

  Page<Serial> findByProduct_Id(Long productId, Pageable pageable);
}
