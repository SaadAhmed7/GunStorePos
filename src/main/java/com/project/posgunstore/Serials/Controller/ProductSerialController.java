// src/main/java/com/project/posgunstore/Serials/Controller/ProductSerialController.java
package com.project.posgunstore.Serials.Controller;

import com.project.posgunstore.Serials.DTO.*;
import com.project.posgunstore.Serials.Service.SerialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/serials")
@RequiredArgsConstructor
public class ProductSerialController {

  private final SerialService service;

  // POST /api/products/{productId}/serials  (bulk create)
  @PostMapping
  public ResponseEntity<List<SerialResponse>> bulkCreate(@PathVariable Long productId,
                                                         @Valid @RequestBody SerialBulkCreateRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.bulkCreate(productId, req));
  }

  // GET /api/products/{productId}/serials (paged list)
  @GetMapping
  public Page<SerialResponse> list(@PathVariable Long productId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "50") int size) {
    return service.listByProduct(productId, page, size);
  }
}
