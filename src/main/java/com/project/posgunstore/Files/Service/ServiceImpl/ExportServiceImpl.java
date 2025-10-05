// src/main/java/com/project/posgunstore/Files/Service/ServiceImpl/ExportServiceImpl.java
package com.project.posgunstore.Files.Service.ServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.posgunstore.Inventory.Model.InventoryLevel;
import com.project.posgunstore.Inventory.Repository.InventoryLevelRepository;
import com.project.posgunstore.Files.Service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

  private final InventoryLevelRepository levels;
  private final ObjectMapper mapper;

  @Override
  public ResponseEntity<Resource> exportInventoryCsv() {
    List<InventoryLevel> all = levels.findAll();
    StringBuilder sb = new StringBuilder("productId,sku,productName,warehouseId,warehouseCode,quantity,reorderPoint\n");
    for (InventoryLevel il : all) {
      var p = il.getProduct(); var w = il.getWarehouse();
      sb.append(p.getId()).append(',')
        .append(escape(p.getSku())).append(',')
        .append(escape(p.getName())).append(',')
        .append(w.getId()).append(',')
        .append(escape(w.getCode())).append(',')
        .append(il.getQuantity()).append(',')
        .append(il.getReorderPoint()).append('\n');
    }
    byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory.csv")
        .contentType(MediaType.parseMediaType("text/csv"))
        .contentLength(bytes.length)
        .body(new ByteArrayResource(bytes));
  }

  @Override
  public ResponseEntity<Resource> exportInventoryJson() {
    List<InventoryLevel> all = levels.findAll();
    try {
      byte[] bytes = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(all.stream().map(il -> {
        var p = il.getProduct(); var w = il.getWarehouse();
        return new Row(p.getId(), p.getSku(), p.getName(), w.getId(), w.getCode(), il.getQuantity(), il.getReorderPoint());
      }).toList());
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory.json")
          .contentType(MediaType.APPLICATION_JSON)
          .contentLength(bytes.length)
          .body(new ByteArrayResource(bytes));
    } catch (Exception e) {
      throw new RuntimeException("JSON export failed", e);
    }
  }

  private String escape(String s){
    if (s == null) return "";
    if (s.contains(",") || s.contains("\"")) return "\"" + s.replace("\"", "\"\"") + "\"";
    return s;
  }

  private record Row(Long productId, String sku, String productName,
                     Long warehouseId, String warehouseCode, Integer quantity, Integer reorderPoint) { }
}
