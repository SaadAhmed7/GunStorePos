// src/main/java/com/project/posgunstore/Files/Service/FileObjectService.java
package com.project.posgunstore.Files.Service;

import com.project.posgunstore.Files.Model.StoredFile;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileObjectService {
  StoredFile upload(MultipartFile file);
  StoredFile get(String id);
  ResponseEntity<Resource> download(String id);   // if you prefer streaming; or 302 redirect
  String presignedDownloadUrl(String id, long expiresSeconds);
}
