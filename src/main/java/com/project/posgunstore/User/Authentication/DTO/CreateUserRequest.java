package com.project.posgunstore.User.Authentication.DTO;

import com.project.posgunstore.util.ENUM.Role;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private Role role;
    private Set<UUID> stationIds; // assign stations by IDs
}
