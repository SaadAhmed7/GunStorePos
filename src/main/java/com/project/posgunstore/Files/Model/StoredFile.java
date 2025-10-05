// src/main/java/com/project/posgunstore/Files/Model/StoredFile.java
package com.project.posgunstore.Files.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "stored_files")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StoredFile {
  @Id
  @Column(length = 40)            // store UUID without dashes or shortid
  private String id;

  @Column(nullable = false)
  private String s3Key;

  @Column(nullable = false)
  private String contentType;

  @Column(nullable = false)
  private long size;

  @Column(nullable = false)
  private Instant uploadedAt;

  // Optional cached public URL (if you ever set `PUBLIC_READ`)
  private String url;
}
