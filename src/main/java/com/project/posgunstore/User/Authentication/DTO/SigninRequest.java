package com.project.posgunstore.User.Authentication.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SigninRequest {

    // Optional — if you want to track which POS terminal/station the user logs in from
    private UUID stationId;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
