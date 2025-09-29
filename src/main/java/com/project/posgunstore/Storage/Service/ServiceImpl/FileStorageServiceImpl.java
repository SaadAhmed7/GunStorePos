package com.project.posgunstore.Storage.Service.ServiceImpl;

import com.project.posgunstore.Storage.Model.BackupHistory;
import com.project.posgunstore.Storage.Model.ImportHistory;
import com.project.posgunstore.Storage.Model.ProductImage;
import com.project.posgunstore.Storage.Repository.BackupHistoryRepository;
import com.project.posgunstore.Storage.Repository.ImportHistoryRepository;
import com.project.posgunstore.Storage.Repository.ProductImageRepository;
import com.project.posgunstore.Storage.Service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final ProductImageRepository productImageRepo;
    private final ImportHistoryRepository importHistoryRepo;
    private final BackupHistoryRepository backupHistoryRepo;

    @Value("${storage.s3.bucket}")
    private String bucket;

    // --- Product Images ---
    @Override
    public ProductImage uploadProductImage(MultipartFile file, String category) {
        String key = "products/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ) // make it public
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }

        String url = "https://" + bucket + ".sfo3.digitaloceanspaces.com/" + key;

        ProductImage img = ProductImage.builder()
                .fileName(file.getOriginalFilename())
                .category(category)
                .url(url)
                .sizeKb(file.getSize() / 1024)
                .status("active")
                .uploadedAt(LocalDateTime.now())
                .build();

        return productImageRepo.save(img);
    }

    @Override
    public List<ProductImage> getAllProductImages() {
        return productImageRepo.findAll();
    }

    @Override
    public void deleteProductImage(Long id) {
        ProductImage img = productImageRepo.findById(id).orElseThrow();
        String key = img.getUrl().substring(img.getUrl().indexOf(".com/") + 5); // extract key

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);

        productImageRepo.delete(img);
    }

    // --- Imports ---
    @Override
    public void importProducts(MultipartFile file) {
        saveImportHistory(file, "Products", 100, "success");
    }

    @Override
    public void importCustomers(MultipartFile file) {
        saveImportHistory(file, "Customers", 50, "partial");
    }

    @Override
    public void importInventory(MultipartFile file) {
        saveImportHistory(file, "Inventory", 200, "success");
    }

    @Override
    public List<ImportHistory> getImportHistory() {
        return importHistoryRepo.findAll();
    }

    private void saveImportHistory(MultipartFile file, String type, int records, String status) {
        ImportHistory history = ImportHistory.builder()
                .fileName(file.getOriginalFilename())
                .type(type)
                .records(records)
                .status(status)
                .importedAt(LocalDateTime.now())
                .build();
        importHistoryRepo.save(history);
    }

    // --- Backups ---
    @Override
    public String createBackup() {
        String fileName = "backup-" + LocalDateTime.now() + ".sql";
        String key = "backups/" + fileName;

        // TODO: Run pg_dump and upload the file to S3

        backupHistoryRepo.save(BackupHistory.builder()
                .fileName(fileName)
                .sizeKb(1024L)
                .createdAt(LocalDateTime.now())
                .build());

        return "https://" + bucket + ".sfo3.digitaloceanspaces.com/" + key;
    }

    @Override
    public List<BackupHistory> getBackupList() {
        return backupHistoryRepo.findAll();
    }

    @Override
    public String generateBackupDownloadUrl(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key("backups/" + fileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofHours(1))
                .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);

        return presigned.url().toString();
    }

    // --- Templates ---
    @Override
    public String getImportTemplateUrl(String templateName) {
        return "https://" + bucket + ".sfo3.digitaloceanspaces.com/templates/" + templateName;
    }
}
