// src/main/java/com/project/posgunstore/Alerts/Service/ServiceImpl/AlertServiceImpl.java
package com.project.posgunstore.Alerts.Service.ServiceImpl;

import com.project.posgunstore.Alerts.DTO.*;
import com.project.posgunstore.Alerts.Model.AlertSubscription;
import com.project.posgunstore.Alerts.Model.AlertType;
import com.project.posgunstore.Alerts.Model.SubscriptionType;
import com.project.posgunstore.Alerts.Repository.AlertSubscriptionRepository;
import com.project.posgunstore.Alerts.Service.AlertService;
import com.project.posgunstore.Catalog.Model.Product;
import com.project.posgunstore.Inventory.Model.InventoryLevel;
import com.project.posgunstore.Inventory.Repository.InventoryLevelRepository;
import com.project.posgunstore.Inventory.Events.InventoryChangedEvent;
import com.project.posgunstore.Warehouse.Model.Warehouse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertServiceImpl implements AlertService {

  private final InventoryLevelRepository levels;
  private final AlertSubscriptionRepository subs;

  private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  @Override
  @Transactional(readOnly = true)
  public Page<LowStockItem> lowStock(Integer threshold, int page, int size) {
    Page<InventoryLevel> res = levels.lowStock(threshold, PageRequest.of(page, size));
    return res.map(this::toLowStockItem);
  }

  @Override
  public SubscriptionResponse subscribe(SubscribeRequest req) {
    validateTarget(req.subscriptionType(), req.target());
    AlertSubscription s = AlertSubscription.builder()
        .alertType(req.alertType())
        .subscriptionType(req.subscriptionType())
        .target(req.target().trim())
        .active(true)
        .build();
    s = subs.save(s);
    return new SubscriptionResponse(s.getId(), s.getAlertType(), s.getSubscriptionType(), s.getTarget(), s.getActive(), s.getCreatedAt());
  }

  @Override
  public void unsubscribe(Long subscriptionId) {
    AlertSubscription s = subs.findById(subscriptionId)
        .orElseThrow(() -> new EntityNotFoundException("Subscription not found"));
    subs.delete(s);
  }

  // --------- Optional: react to inventory changes and notify subscribers ---------

  @EventListener
  @Transactional(readOnly = true)
  public void onInventoryChanged(InventoryChangedEvent evt) {
    // Query this product/warehouse level; if low, notify
    levels.findByProduct_IdAndWarehouse_Id(evt.productId(), evt.warehouseId()).ifPresent(il -> {
      boolean isLow = il.getQuantity() <= il.getReorderPoint();
      if (isLow) notifyLowStock(il);
    });
  }

  private void notifyLowStock(InventoryLevel il) {
    List<AlertSubscription> active = subs.findByAlertTypeAndActiveTrue(AlertType.LOW_STOCK);
    if (active.isEmpty()) return;

    LowStockItem item = toLowStockItem(il);
    for (AlertSubscription s : active) {
      switch (s.getSubscriptionType()) {
        case EMAIL -> sendEmail(s.getTarget(), item);
        case WEBHOOK -> postWebhook(s.getTarget(), item);
      }
    }
  }

  // ---- naive notifiers (replace with your real mailer / webhook client) ----
  private void sendEmail(String to, LowStockItem item) {
    // integrate your mailer here; for now this is a placeholder
    System.out.printf("LOW-STOCK EMAIL → %s : %s@%s qty=%d rp=%d%n",
        to, item.productName(), item.warehouseCode(), item.quantity(), item.reorderPoint());
  }

  private void postWebhook(String url, LowStockItem item) {
    try {
      // validate URL structure only; real HTTP client not included here
      URI.create(url);
      System.out.printf("LOW-STOCK WEBHOOK → %s : %s@%s qty=%d rp=%d%n",
          url, item.productName(), item.warehouseCode(), item.quantity(), item.reorderPoint());
    } catch (IllegalArgumentException e) {
      // ignore malformed URL at send time
    }
  }

  private void validateTarget(SubscriptionType type, String target) {
    if (type == SubscriptionType.EMAIL && !EMAIL.matcher(target).matches())
      throw new IllegalArgumentException("Invalid email");
    if (type == SubscriptionType.WEBHOOK) {
      try { URI.create(target); } catch (Exception e) { throw new IllegalArgumentException("Invalid webhook URL"); }
    }
  }

  private LowStockItem toLowStockItem(InventoryLevel il) {
    Product p = il.getProduct();
    Warehouse w = il.getWarehouse();
    return new LowStockItem(
        p.getId(), p.getSku(), p.getName(),
        w.getId(), w.getCode(),
        il.getQuantity(), il.getReorderPoint());
  }
}
