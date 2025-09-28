package com.project.posgunstore.SystemConfigurations.DTO;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxSettingsDto {

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax rate must be >= 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "Tax rate must be <= 1 (e.g., 0.15 = 15%)")
    private Double defaultRate;

    private boolean taxIncluded;   // true if prices already include tax
}

