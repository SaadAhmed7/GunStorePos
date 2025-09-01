package com.project.posgunstore.User.Authentication.Service;

import com.project.posgunstore.User.Authentication.DTO.SigninRequest;
import com.project.posgunstore.User.Authentication.DTO.SignupRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<?> signup(SignupRequest req);
    ResponseEntity<?> signin(SigninRequest req, HttpServletRequest request);
    ResponseEntity<?> forgotPassword(String email);
    ResponseEntity<?> resetPassword(String token, String newPassword);
}
