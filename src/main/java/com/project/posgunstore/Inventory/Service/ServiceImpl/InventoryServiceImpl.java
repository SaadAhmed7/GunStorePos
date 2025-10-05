// InventoryServiceImpl.java
package com.project.posgunstore.Inventory.Service.ServiceImpl;

import com.project.posgunstore.Catalog.Model.Product;
import com.project.posgunstore.Catalog.Repository.ProductRepository;
import com.project.posgunstore.Inventory.DTO.*;
import com.project.posgunstore.Inventory.Events.InventoryChangedEvent;
import com.project.posgunstore.Inventory.Model.*;
import com.project.posgunstore.Inventory.Repository.*;
import com.project.posgunstore.Inventory.Service.InventoryService;
import com.project.posgunstore.Warehouse.Model.Warehouse;
import com.project.posgunstore.Warehouse.Repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

  private final InventoryLevelRepository levels;
  private final InventoryAdjustmentRepository adjustments;
  private final InventoryMovementRepository movements;
  private final ProductRepository products;
  private final WarehouseRepository warehouses;
  private final ApplicationEventPublisher publisher;

  @Override @Transactional(readOnly = true)
  public InventoryLevelResponse getLevel(Long productId, Long warehouseId) {
    InventoryLevel il = levels.findByProduct_IdAndWarehouse_Id(productId, warehouseId)
        .orElseThrow(() -> new EntityNotFoundException("Inventory level not found"));
    return toLevel(il);
  }

  @Override
  public InventoryAdjustmentResponse adjust(InventoryAdjustRequest req) {
    Product p = products.findById(req.productId())
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    Warehouse w = warehouses.findById(req.warehouseId())
        .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

    // get or create level row
    InventoryLevel il = levels.findByProduct_IdAndWarehouse_Id(p.getId(), w.getId())
        .orElse(InventoryLevel.builder().product(p).warehouse(w).quantity(0).reorderPoint(0).build());

    int newQty = il.getQuantity() + req.delta();
    if (newQty < 0) throw new IllegalStateException("Insufficient stock");

    il.setQuantity(newQty);
    levels.save(il);

    InventoryAdjustment a = InventoryAdjustment.builder()
        .product(p).warehouse(w).delta(req.delta()).reason(req.reason()).build();
    a = adjustments.save(a);
    publisher.publishEvent(new InventoryChangedEvent(p.getId(), w.getId()));


    return new InventoryAdjustmentResponse(a.getId(), p.getId(), w.getId(), a.getDelta(), a.getReason(), a.getCreatedAt());
  }

  @Override
  public InventoryMovementResponse transfer(InventoryMovementRequest req) {
    if (req.fromWarehouseId().equals(req.toWarehouseId()))
      throw new IllegalArgumentException("from and to warehouses must differ");

    // debit
    adjust(new InventoryAdjustRequest(req.productId(), req.fromWarehouseId(), -Math.abs(req.quantity()),
        "Transfer to WH#" + req.toWarehouseId()));
    // credit
    InventoryAdjustmentResponse credit = adjust(new InventoryAdjustRequest(
        req.productId(), req.toWarehouseId(), Math.abs(req.quantity()),
        "Transfer from WH#" + req.fromWarehouseId()));

    // create movement record
    Product p = products.getReferenceById(req.productId());
    Warehouse fromWh = warehouses.getReferenceById(req.fromWarehouseId());
    Warehouse toWh = warehouses.getReferenceById(req.toWarehouseId());

    InventoryMovement m = InventoryMovement.builder()
        .product(p).fromWarehouse(fromWh).toWarehouse(toWh)
        .quantity(Math.abs(req.quantity())).build();
    m = movements.save(m);
    publisher.publishEvent(new InventoryChangedEvent(req.productId(), req.fromWarehouseId()));
    publisher.publishEvent(new InventoryChangedEvent(req.productId(), req.toWarehouseId()));


    return new InventoryMovementResponse(m.getId(), p.getId(), fromWh.getId(), toWh.getId(), m.getQuantity(), m.getCreatedAt());
  }

  @Override @Transactional(readOnly = true)
  public Page<InventoryAdjustmentResponse> listAdjustments(Long productId, Long warehouseId,
                                                           Instant from, Instant to,
                                                           int page, int size) {
    Page<InventoryAdjustment> res = adjustments.search(productId, warehouseId, from, to,
        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    return res.map(a -> new InventoryAdjustmentResponse(
        a.getId(), a.getProduct().getId(), a.getWarehouse().getId(), a.getDelta(), a.getReason(), a.getCreatedAt()));
  }

  @Override @Transactional(readOnly = true)
  public Page<InventoryMovementResponse> listMovements(Long productId, Long fromWarehouseId, Long toWarehouseId,
                                                       Instant from, Instant to,
                                                       int page, int size) {
    Page<InventoryMovement> res = movements.search(productId, fromWarehouseId, toWarehouseId, from, to,
        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    return res.map(m -> new InventoryMovementResponse(
        m.getId(), m.getProduct().getId(), m.getFromWarehouse().getId(), m.getToWarehouse().getId(),
        m.getQuantity(), m.getCreatedAt()));
  }

  @Override @Transactional(readOnly = true)
  public Page<InventoryLevelResponse> lowStock(Integer threshold, int page, int size) {
    return levels.lowStock(threshold, PageRequest.of(page, size))
        .map(this::toLevel);
  }

  private InventoryLevelResponse toLevel(InventoryLevel il) {
    return new InventoryLevelResponse(il.getProduct().getId(), il.getWarehouse().getId(),
        il.getQuantity(), il.getReorderPoint());
  }
}
