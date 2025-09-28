package com.project.posgunstore.Storage.Repository;

import com.project.posgunstore.Storage.Model.BackupHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupHistoryRepository extends JpaRepository<BackupHistory, Long> {}
