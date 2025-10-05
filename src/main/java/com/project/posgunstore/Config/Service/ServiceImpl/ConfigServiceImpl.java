
// src/main/java/com/project/posgunstore/Config/Service/ServiceImpl/ConfigServiceImpl.java
package com.project.posgunstore.Config.Service.ServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.posgunstore.Config.DTO.InventoryRules;
import com.project.posgunstore.Config.DTO.TaxConfig;
import com.project.posgunstore.Config.Model.AppConfig;
import com.project.posgunstore.Config.Repository.AppConfigRepository;
import com.project.posgunstore.Config.Service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class ConfigServiceImpl implements ConfigService {

  private static final String KEY_TAX = "tax";
  private static final String KEY_INV = "inventory";

  private final AppConfigRepository repo;
  private final ObjectMapper mapper = new ObjectMapper();

  @Override @Transactional(readOnly = true)
  public TaxConfig getTax() {
    return repo.findById(KEY_TAX).map(this::readTax)
        .orElseGet(() -> new TaxConfig(new BigDecimal("0.00"), Boolean.FALSE));
  }

  @Override
  public TaxConfig setTax(TaxConfig cfg) {
    AppConfig row = repo.findById(KEY_TAX).orElseGet(() -> AppConfig.builder().key(KEY_TAX).valueJson("{}").build());
    row.setValueJson(write(cfg));
    return readTax(repo.save(row));
  }

  @Override @Transactional(readOnly = true)
  public InventoryRules getInventoryRules() {
    return repo.findById(KEY_INV).map(this::readInv)
        .orElseGet(() -> new InventoryRules(false, 0, 0));
  }

  @Override
  public InventoryRules setInventoryRules(InventoryRules rules) {
    AppConfig row = repo.findById(KEY_INV).orElseGet(() -> AppConfig.builder().key(KEY_INV).valueJson("{}").build());
    row.setValueJson(write(rules));
    return readInv(repo.save(row));
  }

  // ------- json helpers -------
  private TaxConfig readTax(AppConfig row) {
    try { return mapper.readValue(row.getValueJson(), TaxConfig.class); }
    catch (Exception e) { return new TaxConfig(new BigDecimal("0.00"), Boolean.FALSE); }
  }
  private InventoryRules readInv(AppConfig row) {
    try { return mapper.readValue(row.getValueJson(), InventoryRules.class); }
    catch (Exception e) { return new InventoryRules(false, 0, 0); }
  }
  private String write(Object o) {
    try { return mapper.writeValueAsString(o); }
    catch (Exception e) { throw new RuntimeException("Config serialization failed", e); }
  }
}
