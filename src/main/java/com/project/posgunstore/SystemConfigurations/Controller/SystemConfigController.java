package com.project.posgunstore.SystemConfigurations.Controller;

import com.project.posgunstore.SystemConfigurations.DTO.BusinessRulesDto;
import com.project.posgunstore.SystemConfigurations.DTO.ReceiptSettingsDto;
import com.project.posgunstore.SystemConfigurations.DTO.StoreSettingsDto;
import com.project.posgunstore.SystemConfigurations.DTO.TaxSettingsDto;
import com.project.posgunstore.SystemConfigurations.Service.SystemConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
public class SystemConfigController {

    private final SystemConfigService configService;

    public SystemConfigController(SystemConfigService configService) {
        this.configService = configService;
    }

    @GetMapping("/store")
    public ResponseEntity<StoreSettingsDto> getStoreSettings() {
        return ResponseEntity.ok(configService.getStoreSettings());
    }

    @PostMapping("/store")
    public ResponseEntity<Void> updateStoreSettings(@RequestBody StoreSettingsDto dto) {
        configService.updateStoreSettings(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tax")
    public ResponseEntity<TaxSettingsDto> getTaxSettings() {
        return ResponseEntity.ok(configService.getTaxSettings());
    }

    @PostMapping("/tax")
    public ResponseEntity<Void> updateTaxSettings(@RequestBody TaxSettingsDto dto) {
        configService.updateTaxSettings(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rules")
    public ResponseEntity<BusinessRulesDto> getBusinessRules() {
        return ResponseEntity.ok(configService.getBusinessRules());
    }

    @PostMapping("/rules")
    public ResponseEntity<Void> updateBusinessRules(@RequestBody BusinessRulesDto dto) {
        configService.updateBusinessRules(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/receipt")
    public ResponseEntity<ReceiptSettingsDto> getReceiptSettings() {
        return ResponseEntity.ok(configService.getReceiptSettings());
    }

    @PostMapping("/receipt")
    public ResponseEntity<Void> updateReceiptSettings(@RequestBody ReceiptSettingsDto dto) {
        configService.updateReceiptSettings(dto);
        return ResponseEntity.ok().build();
    }
}
