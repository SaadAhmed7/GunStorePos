package com.project.posgunstore.SystemConfigurations.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptSettingsDto {

    @NotBlank(message = "Header text is required")
    private String headerText;

    private String footerText;

    private String supportEmail;

    private String supportPhone;

    private String logoUrl;
}
