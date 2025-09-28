package com.project.posgunstore.SystemConfigurations.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportRecordDto {
    private String id;
    private String type; // PRODUCTS, CUSTOMERS, INVENTORY_ADJ
    private String fileName;
    private String status; // PENDING, RUNNING, SUCCESS, FAILED
    private String message; // error or summary
    private Instant createdAt;
    // getters + setters
}
