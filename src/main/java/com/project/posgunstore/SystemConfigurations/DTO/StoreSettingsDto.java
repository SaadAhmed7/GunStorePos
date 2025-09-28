package com.project.posgunstore.SystemConfigurations.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreSettingsDto {

    @NotBlank(message = "Store name is required")
    private String storeName;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    @NotNull(message = "Contact number is required")
    private String contactNumber;

    private String address;

    private String email;
}
