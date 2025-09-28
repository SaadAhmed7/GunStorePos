package com.project.posgunstore.SystemConfigurations.Service.ServiceImpl;

import com.project.posgunstore.SystemConfigurations.DTO.*;
import com.project.posgunstore.SystemConfigurations.Model.SystemConfiguration;
import com.project.posgunstore.SystemConfigurations.Repository.SystemConfigurationRepository;
import com.project.posgunstore.SystemConfigurations.Service.SystemConfigService;
import com.project.posgunstore.SystemConfigurations.util.JsonMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigurationRepository repository;
    private final JsonMapperUtil jsonMapper;

    private static final String STORE = "STORE";
    private static final String TAX = "TAX";
    private static final String BUSINESS_RULE = "BUSINESS_RULE";
    private static final String RECEIPT = "RECEIPT";

    // --- Store ---
    @Override
    public StoreSettingsDto getStoreSettings() {
        return repository.findByConfigTypeAndConfigKey(STORE, "store.settings")
                .map(c -> jsonMapper.fromJson(c.getConfigValue(), StoreSettingsDto.class))
                .orElse(StoreSettingsDto.builder()
                        .storeName("Default Store")
                        .currency("USD")
                        .timezone("UTC")
                        .contactNumber("+0000000000")
                        .address("Default Address")
                        .email("support@default.com")
                        .build());
    }

    @Override
    public void updateStoreSettings(StoreSettingsDto dto) {
        upsertConfig(STORE, "store.settings", dto);
    }

    // --- Tax ---
    @Override
    public TaxSettingsDto getTaxSettings() {
        return repository.findByConfigTypeAndConfigKey(TAX, "tax.settings")
                .map(c -> jsonMapper.fromJson(c.getConfigValue(), TaxSettingsDto.class))
                .orElse(TaxSettingsDto.builder()
                        .defaultRate(0.15)
                        .taxIncluded(true)
                        .build());
    }

    @Override
    public void updateTaxSettings(TaxSettingsDto dto) {
        upsertConfig(TAX, "tax.settings", dto);
    }

    // --- Business Rules ---
    @Override
    public BusinessRulesDto getBusinessRules() {
        return repository.findByConfigTypeAndConfigKey(BUSINESS_RULE, "business.rules")
                .map(c -> jsonMapper.fromJson(c.getConfigValue(), BusinessRulesDto.class))
                .orElse(BusinessRulesDto.builder()
                        .inventoryPolicy("FIFO")
                        .maxDiscountPercentage(100)
                        .allowReturns(true)
                        .returnWindowDays(30)
                        .build());
    }

    @Override
    public void updateBusinessRules(BusinessRulesDto dto) {
        upsertConfig(BUSINESS_RULE, "business.rules", dto);
    }

    // --- Receipt ---
    @Override
    public ReceiptSettingsDto getReceiptSettings() {
        return repository.findByConfigTypeAndConfigKey(RECEIPT, "receipt.settings")
                .map(c -> jsonMapper.fromJson(c.getConfigValue(), ReceiptSettingsDto.class))
                .orElse(ReceiptSettingsDto.builder()
                        .headerText("Thank you for shopping with us!")
                        .footerText("Visit again!")
                        .supportEmail("support@store.com")
                        .supportPhone("+1111111111")
                        .logoUrl("/assets/logo.png")
                        .build());
    }

    @Override
    public void updateReceiptSettings(ReceiptSettingsDto dto) {
        upsertConfig(RECEIPT, "receipt.settings", dto);
    }

    // --- helper ---
    private <T> void upsertConfig(String type, String key, T dto) {
        SystemConfiguration config = repository.findByConfigTypeAndConfigKey(type, key)
                .orElse(SystemConfiguration.builder()
                        .configType(type)
                        .configKey(key)
                        .createdAt(LocalDateTime.now())
                        .build());

        config.setConfigValue(jsonMapper.toJson(dto));
        config.setUpdatedAt(LocalDateTime.now());

        repository.save(config);
    }
}
