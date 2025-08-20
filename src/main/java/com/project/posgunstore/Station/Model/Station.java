package com.project.posgunstore.Station.Model;

import com.project.posgunstore.Store.Model.Store;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne
    @JoinColumn(name="store_id")
    private Store store;

    private String status;
    // getters/setters
}
