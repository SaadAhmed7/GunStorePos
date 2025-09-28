// ProductImage.java
package com.project.posgunstore.Storage.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_images")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String category;
    private String url;
    private Long sizeKb;

    private String status; // active, processing
    private LocalDateTime uploadedAt;
}
