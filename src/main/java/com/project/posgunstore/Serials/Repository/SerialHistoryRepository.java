// src/main/java/com/project/posgunstore/Serials/Repository/SerialHistoryRepository.java
package com.project.posgunstore.Serials.Repository;

import com.project.posgunstore.Serials.Model.SerialHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SerialHistoryRepository extends JpaRepository<SerialHistory, Long> {
  List<SerialHistory> findBySerial_IdOrderByAtDesc(Long serialId);
}
