package com.project.posgunstore.User.Authentication.DTO;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String username;
    private String role;

    // Optional — if you want to return assigned stations with login
    private List<UUID> assignedStationIds;
}
