package com.project.posgunstore.User.Authentication.ServiceImpl;

import com.project.posgunstore.Config.Security.JwtUtil;
import com.project.posgunstore.LoginHistory.Model.LoginHistory;
import com.project.posgunstore.LoginHistory.Repository.LoginHistoryRepository;
import com.project.posgunstore.PasswordResetToken.Model.PasswordResetToken;
import com.project.posgunstore.PasswordResetToken.Repository.PasswordResetTokenRepository;
import com.project.posgunstore.Station.Model.Station;
import com.project.posgunstore.Station.Repository.StationRepository;
import com.project.posgunstore.User.Authentication.DTO.*;
import com.project.posgunstore.User.Authentication.Service.AuthenticationService;
import com.project.posgunstore.User.Model.User;
import com.project.posgunstore.User.Repository.UserRepository;
import com.project.posgunstore.util.ENUM.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    UserRepository userRepo;
    @Autowired
    StationRepository stationRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    LoginHistoryRepository loginHistoryRepository;

    @Override
    public ResponseEntity<?> signup(SignupRequest req) {
        if(userRepo.existsByUsername(req.getUsername())) return ResponseEntity
                .badRequest()
                .body("Username taken");;
        if(userRepo.existsByEmail(req.getEmail())) return ResponseEntity
                .badRequest()
                .body("Email taken");
        if(!req.getPassword().equals(req.getConfirmPassword())) return ResponseEntity
                .badRequest()
                .body("Passwords do not match");;

        User u = new User();
        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(Role.valueOf(req.getRole())); // validate in real code

        if(req.getAssignedStationIds()!=null) {
            Set<Station> stations = stationRepo.findAllById(req.getAssignedStationIds()).stream().collect(Collectors.toSet());
            u.setAssignedStations(stations);
        }
        userRepo.save(u);
        return ResponseEntity.ok(Map.of("message","user created"));
    }

    @Override
    public ResponseEntity<?> signin(SigninRequest req, HttpServletRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        saveLoginHistory(req.getUsername(), request, true);
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        List<UUID> stationIds = user.getAssignedStations()
                .stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        JwtResponse jwtResponse = JwtResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .assignedStationIds(stationIds)
                .build();

        return ResponseEntity.ok(jwtResponse);
    }

    @Override
    public ResponseEntity<?> forgotPassword(String email) {
        Optional<User> u = userRepo.findByEmail(email);
        if (u.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "If the email exists, a reset link will be sent"));
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken(
                token,
                u.get().getId(),
                Instant.now().plus(1, ChronoUnit.HOURS),
                false
        );

        passwordResetTokenRepository.save(prt);
        // TODO: send email with token
        return ResponseEntity.ok(Map.of("message", "Reset link sent (email sending to be implemented)"));
    }

    @Override
    public ResponseEntity<?> resetPassword(String token, String newPassword){
        var opt = passwordResetTokenRepository.findById(token);

        if (opt.isEmpty() || opt.get().isUsed() || opt.get().getExpiresAt().isBefore(Instant.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid or expired token"));
        }

        var user = userRepo.findById(opt.get().getUserId()).orElseThrow();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        opt.get().setUsed(true);
        passwordResetTokenRepository.save(opt.get());

        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }

    private void saveLoginHistory(String username, HttpServletRequest request, boolean success) {
        LoginHistory history = LoginHistory.builder()
                .username(username)
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .loginTime(LocalDateTime.now())
                .success(success)
                .build();

        loginHistoryRepository.save(history);
    }

    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepo.findAll(); // assuming you have a UserRepository
        return ResponseEntity.ok(users);
    }

    public User addUser(CreateUserRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        Set<Station> stations = new HashSet<>();
        if (req.getStationIds() != null) {
            stations.addAll(stationRepo.findAllById(req.getStationIds()));
        }

        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .assignedStations(stations)
                .enabled(true)
                .build();

        return userRepo.save(user);
    }

    public void softDeleteUser(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setEnabled(false);
        userRepo.save(user);
    }

    public User updateUser(UUID userId, UpdateUserRequest req) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getRole() != null) user.setRole(req.getRole());
        if (req.getEnabled() != null) user.setEnabled(req.getEnabled());

        if (req.getStationIds() != null) {
            user.setAssignedStations(
                    new HashSet<>(stationRepo.findAllById(req.getStationIds()))
            );
        }

        return userRepo.save(user);
    }


}
