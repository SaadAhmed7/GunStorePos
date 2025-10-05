// src/main/java/com/project/posgunstore/Inventory/Events/InventoryChangedEvent.java
package com.project.posgunstore.Inventory.Events;

public record InventoryChangedEvent(Long productId, Long warehouseId) {}
