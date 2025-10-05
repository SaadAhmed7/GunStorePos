// src/main/java/com/project/posgunstore/Barcode/DTO/BarcodeGenerateRequest.java
package com.project.posgunstore.Barcode.DTO;

import jakarta.validation.constraints.*;

public record BarcodeGenerateRequest(
  @NotBlank String content,          // what to encode
  String symbology,                  // CODE_128 | EAN_13 | QR_CODE (default CODE_128)
  @Positive @Max(4096) Integer width,
  @Positive @Max(4096) Integer height,
  @PositiveOrZero @Max(64) Integer margin,
  Boolean includeText                // for 1D barcodes; ignored for QR
) {}
