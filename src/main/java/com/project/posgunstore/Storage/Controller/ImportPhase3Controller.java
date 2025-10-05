package com.project.posgunstore.Storage.Controller;

import com.project.posgunstore.Storage.Service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportPhase3Controller {

    private final FileStorageService service;

    // Phase-3 spec: POST /api/import/products (CSV/XLSX)
    @PostMapping("/products")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void importProducts(@RequestParam("file") MultipartFile file) {
        service.importProducts(file);
    }
}
