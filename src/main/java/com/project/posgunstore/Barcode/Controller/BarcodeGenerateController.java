// src/main/java/com/project/posgunstore/Barcode/Controller/BarcodeController.java
package com.project.posgunstore.Barcode.Controller;

import com.project.posgunstore.Barcode.DTO.BarcodeGenerateRequest;
import com.project.posgunstore.Barcode.DTO.BarcodeGenerateResponse;
import com.project.posgunstore.Barcode.Service.BarcodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/barcode")
@RequiredArgsConstructor
public class BarcodeGenerateController {

  private final BarcodeService service;

  // POST /api/barcode/generate  → JSON (base64 PNG)
  @PostMapping("/generate")
  public BarcodeGenerateResponse generate(@Valid @RequestBody BarcodeGenerateRequest req) {
    return service.generate(req);
  }

  // POST /api/barcode/generate.png → binary PNG (easy for printers)
  @PostMapping(value = "/generate.png", produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<ByteArrayResource> generatePng(@Valid @RequestBody BarcodeGenerateRequest req) {
    byte[] bytes = service.generatePng(req);
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_PNG)
        .header(HttpHeaders.CACHE_CONTROL, "no-store")
        .body(new ByteArrayResource(bytes));
  }
}
