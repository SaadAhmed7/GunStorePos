// src/main/java/com/project/posgunstore/Files/Controller/ExportController.java
package com.project.posgunstore.Files.Controller;

import com.project.posgunstore.Files.Service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

  private final ExportService exporter;

  @PostMapping("/inventory.csv")
  public ResponseEntity<Resource> csv(){ return exporter.exportInventoryCsv(); }

  @PostMapping("/inventory.json")
  public ResponseEntity<Resource> json(){ return exporter.exportInventoryJson(); }
}
