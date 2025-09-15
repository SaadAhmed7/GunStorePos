package com.project.posgunstore.User.Authentication.Service;

import com.project.posgunstore.User.Authentication.DTO.CreateUserRequest;
import com.project.posgunstore.User.Authentication.DTO.SigninRequest;
import com.project.posgunstore.User.Authentication.DTO.SignupRequest;
import com.project.posgunstore.User.Authentication.DTO.UpdateUserRequest;
import com.project.posgunstore.User.Model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AuthenticationService {
    ResponseEntity<?> signup(SignupRequest req);
    ResponseEntity<?> signin(SigninRequest req, HttpServletRequest request);
    ResponseEntity<?> forgotPassword(String email);
    ResponseEntity<?> resetPassword(String token, String newPassword);
    ResponseEntity<?> getAllUsers();
    User addUser(CreateUserRequest req);
    void softDeleteUser(UUID userId);
    void enableUser(UUID userId);
    User updateUser(UUID userId, UpdateUserRequest req);
}
