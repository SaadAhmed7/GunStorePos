// src/main/java/com/project/posgunstore/Config/Service/ConfigService.java
package com.project.posgunstore.Config.Service;

import com.project.posgunstore.Config.DTO.InventoryRules;
import com.project.posgunstore.Config.DTO.TaxConfig;

public interface ConfigService {
  TaxConfig getTax();
  TaxConfig setTax(TaxConfig cfg);

  InventoryRules getInventoryRules();
  InventoryRules setInventoryRules(InventoryRules rules);
}
