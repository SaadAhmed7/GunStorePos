package com.project.posgunstore.Store.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String phone;
    private String address;
    private String city;
    private String status;
    private String zipCode;
    private String email;
    private String website;
}
