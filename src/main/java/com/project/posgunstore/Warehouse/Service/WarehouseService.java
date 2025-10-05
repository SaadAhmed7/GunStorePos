// WarehouseService.java
package com.project.posgunstore.Warehouse.Service;

import com.project.posgunstore.Warehouse.DTO.*;
import org.springframework.data.domain.Page;

public interface WarehouseService {
  WarehouseResponse create(WarehouseCreateRequest req);
  Page<WarehouseResponse> list(int page, int size);
  WarehouseResponse get(Long id);
  WarehouseResponse update(Long id, WarehouseUpdateRequest req);
  void delete(Long id);
}
