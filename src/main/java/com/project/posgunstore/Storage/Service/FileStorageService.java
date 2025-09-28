package com.project.posgunstore.Storage.Service;

import com.project.posgunstore.Storage.Model.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    ProductImage uploadProductImage(MultipartFile file, String category);
    List<ProductImage> getAllProductImages();
    void deleteProductImage(Long id);

    // Imports
    void importProducts(MultipartFile file);
    void importCustomers(MultipartFile file);
    void importInventory(MultipartFile file);
    List<?> getImportHistory();

    // Backups
    String createBackup();
    List<?> getBackupList();
    String generateBackupDownloadUrl(String fileName);

    // Templates
    String getImportTemplateUrl(String templateName);
}
