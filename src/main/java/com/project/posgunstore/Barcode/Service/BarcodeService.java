// src/main/java/com/project/posgunstore/Barcode/Service/BarcodeService.java
package com.project.posgunstore.Barcode.Service;

import com.project.posgunstore.Barcode.DTO.BarcodeGenerateRequest;
import com.project.posgunstore.Barcode.DTO.BarcodeGenerateResponse;

public interface BarcodeService {
  BarcodeGenerateResponse generate(BarcodeGenerateRequest req);
  byte[] generatePng(BarcodeGenerateRequest req); // raw bytes (for download/print)
}
