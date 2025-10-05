// src/main/java/com/project/posgunstore/Files/Repository/StoredFileRepository.java
package com.project.posgunstore.Files.Repository;

import com.project.posgunstore.Files.Model.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredFileRepository extends JpaRepository<StoredFile, String> { }
