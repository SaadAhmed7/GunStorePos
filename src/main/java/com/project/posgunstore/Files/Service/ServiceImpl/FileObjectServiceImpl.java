// src/main/java/com/project/posgunstore/Files/Service/ServiceImpl/FileObjectServiceImpl.java
package com.project.posgunstore.Files.Service.ServiceImpl;

import com.project.posgunstore.Files.Model.StoredFile;
import com.project.posgunstore.Files.Repository.StoredFileRepository;
import com.project.posgunstore.Files.Service.FileObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileObjectServiceImpl implements FileObjectService {

  private final S3Client s3;
  private final S3Presigner presigner;
  private final StoredFileRepository files;

  @Value("${storage.s3.bucket}")
  private String bucket;

  @Override
  public StoredFile upload(MultipartFile file) {
    try {
      String id = UUID.randomUUID().toString().replace("-", "");
      String key = "uploads/" + id + "-" + file.getOriginalFilename();

      PutObjectRequest put = PutObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .contentType(file.getContentType())
          .acl(ObjectCannedACL.PRIVATE)
          .build();

      s3.putObject(put, RequestBody.fromBytes(file.getBytes()));

      StoredFile sf = StoredFile.builder()
          .id(id)
          .s3Key(key)
          .contentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType())
          .size(file.getSize())
          .uploadedAt(Instant.now())
          .build();

      return files.save(sf);
    } catch (Exception e) {
      throw new RuntimeException("Upload failed", e);
    }
  }

  @Override
  public StoredFile get(String id) {
    return files.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
  }

  @Override
  public ResponseEntity<Resource> download(String id) {
    StoredFile sf = get(id);
    GetObjectRequest get = GetObjectRequest.builder().bucket(bucket).key(sf.getS3Key()).build();
    ResponseInputStream<GetObjectResponse> stream = s3.getObject(get);

    String filename = sf.getS3Key().substring(sf.getS3Key().lastIndexOf('/') + 1);
    String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
        .contentType(MediaType.parseMediaType(sf.getContentType()))
        .contentLength(sf.getSize())
        .body(new InputStreamResource(stream));
  }

  @Override
  public String presignedDownloadUrl(String id, long expiresSeconds) {
    StoredFile sf = get(id);
    GetObjectRequest get = GetObjectRequest.builder().bucket(bucket).key(sf.getS3Key()).build();
    GetObjectPresignRequest pre = GetObjectPresignRequest.builder()
        .getObjectRequest(get)
        .signatureDuration(Duration.ofSeconds(Math.max(expiresSeconds, 60)))
        .build();
    return presigner.presignGetObject(pre).url().toString();
  }
}
