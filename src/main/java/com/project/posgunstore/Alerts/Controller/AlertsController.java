// src/main/java/com/project/posgunstore/Alerts/Controller/AlertsController.java
package com.project.posgunstore.Alerts.Controller;

import com.project.posgunstore.Alerts.DTO.*;
import com.project.posgunstore.Alerts.Service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertsController {

  private final AlertService service;

  // GET /api/alerts/low-stock?threshold={n}
  @GetMapping("/low-stock")
  public Page<LowStockItem> lowStock(@RequestParam(required = false) Integer threshold,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "50") int size) {
    return service.lowStock(threshold, page, size);
  }

  // POST /api/alerts/subscriptions
  @PostMapping("/subscriptions")
  public ResponseEntity<SubscriptionResponse> subscribe(@Valid @RequestBody SubscribeRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.subscribe(req));
  }

  // DELETE /api/alerts/subscriptions/{subscriptionId}
  @DeleteMapping("/subscriptions/{subscriptionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unsubscribe(@PathVariable Long subscriptionId) {
    service.unsubscribe(subscriptionId);
  }
}
