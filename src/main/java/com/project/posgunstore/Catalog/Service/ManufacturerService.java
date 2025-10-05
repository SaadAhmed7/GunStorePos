// ManufacturerService.java
package com.project.posgunstore.Catalog.Service;

import com.project.posgunstore.Catalog.DTO.*;
import org.springframework.data.domain.Page;

public interface ManufacturerService {
  ManufacturerResponse create(ManufacturerCreateRequest req);
  Page<ManufacturerResponse> list(int page, int size, String q);
  ManufacturerResponse get(Long id);
  ManufacturerResponse update(Long id, ManufacturerUpdateRequest req);
  void delete(Long id);
}
