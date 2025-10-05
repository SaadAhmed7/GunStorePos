// src/main/java/com/project/posgunstore/Files/Service/ExportService.java
package com.project.posgunstore.Files.Service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface ExportService {
  ResponseEntity<Resource> exportInventoryCsv();
  ResponseEntity<Resource> exportInventoryJson();
}
