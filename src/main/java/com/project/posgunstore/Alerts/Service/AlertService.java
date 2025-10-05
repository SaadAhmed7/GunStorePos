// src/main/java/com/project/posgunstore/Alerts/Service/AlertService.java
package com.project.posgunstore.Alerts.Service;

import com.project.posgunstore.Alerts.DTO.*;
import org.springframework.data.domain.Page;

public interface AlertService {
  // query
  Page<LowStockItem> lowStock(Integer threshold, int page, int size);

  // subscriptions
  SubscriptionResponse subscribe(SubscribeRequest req);
  void unsubscribe(Long subscriptionId);
}
