// src/main/java/com/project/posgunstore/Config/Repository/AppConfigRepository.java
package com.project.posgunstore.Config.Repository;

import com.project.posgunstore.Config.Model.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppConfigRepository extends JpaRepository<AppConfig, String> { }
