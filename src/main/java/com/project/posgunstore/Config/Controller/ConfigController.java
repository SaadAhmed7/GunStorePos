//// src/main/java/com/project/posgunstore/Config/Controller/ConfigController.java
//package com.project.posgunstore.Config.Controller;
//
//import com.project.posgunstore.Config.DTO.InventoryRules;
//import com.project.posgunstore.Config.DTO.TaxConfig;
//import com.project.posgunstore.Config.Service.ConfigService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/config")
//@RequiredArgsConstructor
//public class ConfigController {
//
//  private final ConfigService service;
//
//  @GetMapping("/tax")
//  public TaxConfig getTax() { return service.getTax(); }
//
//  @PutMapping("/tax")
//  public TaxConfig setTax(@Valid @RequestBody TaxConfig cfg) { return service.setTax(cfg); }
//
//  @GetMapping("/inventory")
//  public InventoryRules getInventory() { return service.getInventoryRules(); }
//
//  @PutMapping("/inventory")
//  public InventoryRules setInventory(@Valid @RequestBody InventoryRules rules) {
//    return service.setInventoryRules(rules);
//  }
//}
