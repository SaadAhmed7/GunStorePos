package com.project.posgunstore.PasswordResetToken.Repository;

import com.project.posgunstore.PasswordResetToken.Model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {}