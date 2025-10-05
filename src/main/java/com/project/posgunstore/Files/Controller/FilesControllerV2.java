// src/main/java/com/project/posgunstore/Files/Controller/FilesControllerV2.java
package com.project.posgunstore.Files.Controller;

import com.project.posgunstore.Files.Model.StoredFile;
import com.project.posgunstore.Files.Service.FileObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FilesControllerV2 {

  private final FileObjectService files;

  // POST /api/files/upload  (multipart)
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
    StoredFile sf = files.upload(file);
    return Map.of(
        "fileId", sf.getId(),
        "contentType", sf.getContentType(),
        "size", String.valueOf(sf.getSize())
    );
  }

  // GET /api/files/{fileId}/download
  @GetMapping("/{fileId}/download")
  public ResponseEntity<Resource> download(@PathVariable String fileId,
                                           @RequestParam(required = false) Boolean redirect) {
    if (Boolean.TRUE.equals(redirect)) {
      String url = files.presignedDownloadUrl(fileId, 3600);
      return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, url).build();
    }
    return files.download(fileId);
  }
}
