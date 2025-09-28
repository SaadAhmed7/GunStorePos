// ImportHistory.java
package com.project.posgunstore.Storage.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "import_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String type;   // Products, Customers, Inventory
    private int records;
    private String status; // success, partial, error
    private LocalDateTime importedAt;
}
