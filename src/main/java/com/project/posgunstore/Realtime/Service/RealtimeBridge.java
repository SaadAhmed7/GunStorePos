// src/main/java/com/project/posgunstore/Realtime/Service/RealtimeBridge.java
package com.project.posgunstore.Realtime.Service;

import com.project.posgunstore.Inventory.Events.InventoryChangedEvent;
import com.project.posgunstore.Inventory.Model.InventoryLevel;
import com.project.posgunstore.Inventory.Repository.InventoryLevelRepository;
import com.project.posgunstore.Realtime.Controller.RealtimeController;
import com.project.posgunstore.Realtime.Events.InventoryEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RealtimeBridge {

  private final InventoryLevelRepository levels;
  private final SimpMessagingTemplate ws;
  private final RealtimeController sse;

  @EventListener
  public void onInventoryChanged(InventoryChangedEvent evt) {
    levels.findByProduct_IdAndWarehouse_Id(evt.productId(), evt.warehouseId())
        .ifPresent(il -> {
          InventoryEventPayload payload = new InventoryEventPayload(
              il.getProduct().getId(),
              il.getWarehouse().getId(),
              il.getQuantity(),
              il.getReorderPoint(),
              Instant.now()
          );
          // push WS
          ws.convertAndSend("/topic/inventory", payload);
          // push SSE
          sse.broadcast(payload);
        });
  }
}
