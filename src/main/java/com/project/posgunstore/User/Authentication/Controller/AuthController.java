package com.project.posgunstore.User.Authentication.Controller;

import com.project.posgunstore.User.Authentication.DTO.CreateUserRequest;
import com.project.posgunstore.User.Authentication.DTO.SigninRequest;
import com.project.posgunstore.User.Authentication.DTO.SignupRequest;
import com.project.posgunstore.User.Authentication.Service.AuthenticationService;
import com.project.posgunstore.User.Model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        return authenticationService.signup(req);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody SigninRequest req, HttpServletRequest request) {
        return authenticationService.signin(req, request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        return authenticationService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        return authenticationService.resetPassword(token, newPassword);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return authenticationService.getAllUsers();
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody CreateUserRequest req) {
        User newUser = authenticationService.addUser(req);
        return ResponseEntity.ok(newUser);
    }

    @PatchMapping("/users/{id}/disable")
    public ResponseEntity<?> softDeleteUser(@PathVariable UUID id) {
        authenticationService.softDeleteUser(id);
        return ResponseEntity.ok("User disabled successfully");
    }

}
