// src/main/java/com/project/posgunstore/Search/Controller/BarcodeController.java
package com.project.posgunstore.Search.Controller;

import com.project.posgunstore.Catalog.DTO.ProductResponse;
import com.project.posgunstore.Catalog.Model.Product;
import com.project.posgunstore.Catalog.Repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/barcode")
@RequiredArgsConstructor
public class BarcodeLookupController {

  private final ProductRepository products;

  @GetMapping("/lookup/{barcode}")
  public ProductResponse lookup(@PathVariable String barcode) {
    Product p = products.findByBarcode(barcode)
        .orElseThrow(() -> new EntityNotFoundException("Product not found for barcode"));
    return new ProductResponse(
        p.getId(), p.getSku(), p.getName(), p.getDescription(), p.getBarcode(),
        p.getCategory() == null ? null : p.getCategory().getId(),
        p.getCategory() == null ? null : p.getCategory().getName(),
        p.getManufacturer() == null ? null : p.getManufacturer().getId(),
        p.getManufacturer() == null ? null : p.getManufacturer().getName(),
        p.getCost(), p.getPrice(), p.getIsSerialized(), p.getIsActive(),
        p.getImageUrl(), p.getVersion(), p.getCreatedAt(), p.getUpdatedAt());
  }
}
