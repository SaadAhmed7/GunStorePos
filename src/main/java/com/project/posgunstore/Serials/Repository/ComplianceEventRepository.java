// src/main/java/com/project/posgunstore/Serials/Repository/ComplianceEventRepository.java
package com.project.posgunstore.Serials.Repository;

import com.project.posgunstore.Serials.Model.ComplianceEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplianceEventRepository extends JpaRepository<ComplianceEvent, Long> { }
