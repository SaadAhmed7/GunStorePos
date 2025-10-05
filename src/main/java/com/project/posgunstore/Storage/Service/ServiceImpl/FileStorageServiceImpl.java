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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String fileName = "backup-" + timestamp + ".sql";
        String key = "backups/" + fileName;

        try {
            // 1. Run pg_dump command
            ProcessBuilder pb = new ProcessBuilder(
                    "pg_dump",
                    "-h", "localhost",          // change if DB is remote
                    "-U", "postgres",           // username
                    "-d", "gunpos"              // database name
            );
            pb.environment().put("PGPASSWORD", "postgres"); // set password here
            Process process = pb.start();

            // 2. Capture pg_dump output into byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (InputStream is = process.getInputStream()) {
                is.transferTo(baos);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("pg_dump failed with exit code " + exitCode);
            }

            byte[] backupBytes = baos.toByteArray();

            // 3. Upload to DigitalOcean Spaces
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("application/sql")
                    .acl(ObjectCannedACL.PRIVATE) // backups should stay private
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(backupBytes));

            // 4. Save backup history
            backupHistoryRepo.save(BackupHistory.builder()
                    .fileName(fileName)
                    .sizeKb((long) (backupBytes.length / 1024))
                    .createdAt(LocalDateTime.now())
                    .build());

            // 5. Return URL
            return "https://" + bucket + ".sfo3.digitaloceanspaces.com/" + key;

        } catch (Exception e) {
            throw new RuntimeException("Backup failed", e);
        }
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

    @Override
    public String storeGeneric(MultipartFile file, String folder) {
        String safeFolder = (folder == null || folder.isBlank()) ? "uploads" : folder.replaceAll("^/+", "");
        String key = safeFolder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ) // make public for easy access; flip to PRIVATE if needed
                    .build();
            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

    @Override
    public String generateFileDownloadUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofHours(1))
                .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);
        return presigned.url().toString();
    }
}
