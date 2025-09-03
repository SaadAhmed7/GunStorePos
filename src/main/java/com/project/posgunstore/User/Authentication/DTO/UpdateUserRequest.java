package com.project.posgunstore.User.Authentication.DTO;

import com.project.posgunstore.util.ENUM.Role;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Boolean enabled;             // allow enabling/disabling user
    private Set<UUID> stationIds;        // assign stations
}
