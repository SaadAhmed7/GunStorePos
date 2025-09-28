package com.project.posgunstore.SystemConfigurations.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessRulesDto {

    @NotBlank(message = "Inventory policy is required")
    private String inventoryPolicy;   // e.g. FIFO, LIFO, FEFO

    @Min(value = 0, message = "Max discount cannot be negative")
    private Integer maxDiscountPercentage;

    private boolean allowReturns;

    private Integer returnWindowDays;  // days allowed for returns
}
