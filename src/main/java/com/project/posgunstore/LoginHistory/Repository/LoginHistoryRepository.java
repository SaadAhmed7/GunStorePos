package com.project.posgunstore.LoginHistory.Repository;

import com.project.posgunstore.LoginHistory.Model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
}
