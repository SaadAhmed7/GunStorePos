// src/main/java/com/project/posgunstore/Serials/Service/SerialService.java
package com.project.posgunstore.Serials.Service;

import com.project.posgunstore.Serials.DTO.*;
import com.project.posgunstore.Serials.Model.SerialStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SerialService {
  // Create
  List<SerialResponse> bulkCreate(Long productId, SerialBulkCreateRequest req);

  // Read
  Page<SerialResponse> listByProduct(Long productId, int page, int size);
  SerialResponse get(Long serialId);
  Page<SerialResponse> search(String q, Long productId, SerialStatus status, int page, int size);

  // Update
  SerialResponse updateStatus(Long serialId, SerialUpdateRequest req);

  // Verify
  SerialVerifyResponse verify(String serialNumber);

  // History & compliance
  List<SerialHistoryItem> history(Long serialId);
  void addComplianceEvent(Long serialId, ComplianceEventRequest req);
}
