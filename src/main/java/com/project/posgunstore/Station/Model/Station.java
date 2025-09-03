package com.project.posgunstore.Station.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.posgunstore.Store.Model.Store;
import com.project.posgunstore.User.Model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Station {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;     // e.g. Front Counter, Customer Service
    private String ipAddress;       // e.g. 192.168.1.101
    private String printer;         // e.g. HP LaserJet Pro
    private String cashDrawer;

    @ManyToOne
    @JoinColumn(name="store_id")
    private Store store;

    private String status;

    @ManyToMany(mappedBy = "assignedStations", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> assignedUsers = new HashSet<>();
}
