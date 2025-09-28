package com.project.posgunstore.SystemConfigurations.Service;

import com.project.posgunstore.SystemConfigurations.DTO.BusinessRulesDto;
import com.project.posgunstore.SystemConfigurations.DTO.ReceiptSettingsDto;
import com.project.posgunstore.SystemConfigurations.DTO.StoreSettingsDto;
import com.project.posgunstore.SystemConfigurations.DTO.TaxSettingsDto;

public interface SystemConfigService {
    StoreSettingsDto getStoreSettings();
    void updateStoreSettings(StoreSettingsDto dto);

    TaxSettingsDto getTaxSettings();
    void updateTaxSettings(TaxSettingsDto dto);

    BusinessRulesDto getBusinessRules();
    void updateBusinessRules(BusinessRulesDto dto);

    ReceiptSettingsDto getReceiptSettings();
    void updateReceiptSettings(ReceiptSettingsDto dto);
}
