// BackupHistory.java
package com.project.posgunstore.Storage.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "backup_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BackupHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private Long sizeKb;
    private LocalDateTime createdAt;
}
