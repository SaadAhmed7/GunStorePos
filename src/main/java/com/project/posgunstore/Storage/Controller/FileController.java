package com.project.posgunstore.Storage.Controller;

import com.project.posgunstore.Storage.Model.ProductImage;
import com.project.posgunstore.Storage.Service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService service;
    @Value("${storage.s3.bucket}")
    private String bucket;

    // --- Product Images ---
    @PostMapping("/images/upload")
    public ProductImage uploadImage(@RequestParam MultipartFile file,
                                    @RequestParam String category) {
        return service.uploadProductImage(file, category);
    }

    @GetMapping("/images")
    public List<ProductImage> getImages() {
        return service.getAllProductImages();
    }

    @DeleteMapping("/images/{id}")
    public void deleteImage(@PathVariable Long id) {
        service.deleteProductImage(id);
    }

    // --- Imports ---
    @PostMapping("/import/products")
    public void importProducts(@RequestParam MultipartFile file) {
        service.importProducts(file);
    }

    @PostMapping("/import/customers")
    public void importCustomers(@RequestParam MultipartFile file) {
        service.importCustomers(file);
    }

    @PostMapping("/import/inventory")
    public void importInventory(@RequestParam MultipartFile file) {
        service.importInventory(file);
    }

    @GetMapping("/import/history")
    public Object getImportHistory() {
        return service.getImportHistory();
    }

    // --- Backups ---
    @PostMapping("/backup/create")
    public String createBackup() {
        return service.createBackup();
    }

    @GetMapping("/backup/list")
    public Object getBackups() {
        return service.getBackupList();
    }

    @GetMapping("/backup/download/{fileName}")
    public String downloadBackup(@PathVariable String fileName) {
        return service.generateBackupDownloadUrl(fileName);
    }

    // --- Templates ---
    @GetMapping("/templates/{templateName}")
    public String getTemplate(@PathVariable String templateName) {
        return service.getImportTemplateUrl(templateName);
    }

    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "folder", required = false) String folder) {
        String key = service.storeGeneric(file, folder);
        // if you kept PUBLIC_READ, you can also directly compose a URL:
        String url = "https://" + bucket + ".sfo3.digitaloceanspaces.com/" + key;
        return Map.of("fileId", key, "url", url);
    }

    // GET /api/files/{fileId}/download  → returns presigned URL (1h)
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Map<String, String>> download(@PathVariable String fileId) {
        String url = service.generateFileDownloadUrl(fileId);
        return ResponseEntity.ok(Map.of("downloadUrl", url));
    }

}
