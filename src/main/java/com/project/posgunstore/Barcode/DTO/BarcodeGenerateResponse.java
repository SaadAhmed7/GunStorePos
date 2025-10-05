// src/main/java/com/project/posgunstore/Barcode/DTO/BarcodeGenerateResponse.java
package com.project.posgunstore.Barcode.DTO;

public record BarcodeGenerateResponse(
  String symbology,
  int width,
  int height,
  String mediaType,      // image/png
  String base64Png       // data with no prefix; use data:image/png;base64,<...> in UI
) {}
