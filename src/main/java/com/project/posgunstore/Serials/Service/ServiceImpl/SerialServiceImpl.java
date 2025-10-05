// src/main/java/com/project/posgunstore/Serials/Service/ServiceImpl/SerialServiceImpl.java
package com.project.posgunstore.Serials.Service.ServiceImpl;

import com.project.posgunstore.Catalog.Model.Product;
import com.project.posgunstore.Catalog.Repository.ProductRepository;
import com.project.posgunstore.Inventory.DTO.InventoryAdjustRequest;
import com.project.posgunstore.Inventory.DTO.InventoryMovementRequest;
import com.project.posgunstore.Inventory.Service.InventoryService;
import com.project.posgunstore.Serials.DTO.*;
import com.project.posgunstore.Serials.Model.*;
import com.project.posgunstore.Serials.Repository.*;
import com.project.posgunstore.Warehouse.Model.Warehouse;
import com.project.posgunstore.Warehouse.Repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SerialServiceImpl implements com.project.posgunstore.Serials.Service.SerialService {

  private final SerialRepository serials;
  private final SerialHistoryRepository history;
  private final ComplianceEventRepository complianceEvents;
  private final ProductRepository products;
  private final WarehouseRepository warehouses;
  private final InventoryService inventory;

  @Override
  public List<SerialResponse> bulkCreate(Long productId, SerialBulkCreateRequest req) {
    Product product = products.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    if (Boolean.FALSE.equals(product.getIsSerialized())) {
      throw new DataIntegrityViolationException("Product is not marked as serialized");
    }

    Warehouse wh = warehouses.findById(req.warehouseId())
        .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

    // Basic duplicate check
    Set<String> incoming = req.serialNumbers().stream()
        .map(String::trim).filter(s -> !s.isEmpty())
        .collect(Collectors.toCollection(LinkedHashSet::new));
    if (incoming.isEmpty()) throw new DataIntegrityViolationException("No serials provided");

    // Check duplicates against DB
    for (String sn : incoming) {
      if (serials.existsBySerialNumber(sn)) {
        throw new DataIntegrityViolationException("Duplicate serial: " + sn);
      }
    }

    List<Serial> toSave = new ArrayList<>();
    for (String sn : incoming) {
      Serial s = Serial.builder()
          .product(product)
          .serialNumber(sn)
          .status(SerialStatus.AVAILABLE)
          .warehouse(wh)
          .build();
      toSave.add(s);
    }
    List<Serial> saved = serials.saveAll(toSave);

    // Inventory: increment +1 per created AVAILABLE serial
    int count = saved.size();
    inventory.adjust(new InventoryAdjustRequest(productId, wh.getId(), count, "Serials bulk create"));

    // History entries
    List<SerialHistory> h = saved.stream().map(s ->
        SerialHistory.builder()
            .serial(s).at(Instant.now())
            .action("CREATE")
            .details("Created at WH#" + wh.getId()).build()
    ).toList();
    history.saveAll(h);

    return saved.stream().map(this::toResponse).toList();
  }

  @Override @Transactional(readOnly = true)
  public Page<SerialResponse> listByProduct(Long productId, int page, int size) {
    Page<Serial> res = serials.findByProduct_Id(productId, PageRequest.of(page, size, Sort.by("id").descending()));
    return res.map(this::toResponse);
  }

  @Override @Transactional(readOnly = true)
  public SerialResponse get(Long serialId) {
    return serials.findById(serialId).map(this::toResponse)
        .orElseThrow(() -> new EntityNotFoundException("Serial not found"));
  }

  @Override
  public SerialResponse updateStatus(Long serialId, SerialUpdateRequest req) {
    Serial s = serials.findById(serialId)
        .orElseThrow(() -> new EntityNotFoundException("Serial not found"));

    // optimistic lock
    if (!Objects.equals(s.getVersion(), req.version()))
      throw new DataIntegrityViolationException("Version conflict");

    SerialStatus old = s.getStatus();
    SerialStatus now = req.status();

    // Optional move (when AVAILABLE)
    if (req.warehouseId() != null) {
      Warehouse newWh = warehouses.findById(req.warehouseId())
          .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
      Long oldWhId = s.getWarehouse() == null ? null : s.getWarehouse().getId();

      if (oldWhId == null || !Objects.equals(oldWhId, newWh.getId())) {
        // If serial is AVAILABLE → perform inventory transfer of 1
        if (old == SerialStatus.AVAILABLE) {
          if (oldWhId == null) {
            // no old warehouse: just credit to new WH
            inventory.adjust(new InventoryAdjustRequest(s.getProduct().getId(), newWh.getId(), 1,
                "Serial moved (no previous WH)"));
          } else {
            inventory.transfer(new InventoryMovementRequest(
                s.getProduct().getId(), oldWhId, newWh.getId(), 1));
          }
        }
        s.setWarehouse(newWh);
        history.save(SerialHistory.builder()
            .serial(s).at(Instant.now()).action("MOVED")
            .details("from=" + oldWhId + ", to=" + newWh.getId()).build());
      }
    }

    // Status transition → adjust inventory if needed
    if (old != now) {
      applyInventoryForStatusChange(s, old, now);
      s.setStatus(now);
      history.save(SerialHistory.builder()
          .serial(s).at(Instant.now()).action("STATUS_CHANGE")
          .details(old + " → " + now).build());
    }

    Serial saved = serials.save(s);
    return toResponse(saved);
  }

  private void applyInventoryForStatusChange(Serial s, SerialStatus old, SerialStatus now) {
    Long whId = s.getWarehouse() == null ? null : s.getWarehouse().getId();
    Long productId = s.getProduct().getId();

    // AVAILABLE → SOLD/DAMAGED : -1
    if (old == SerialStatus.AVAILABLE && (now == SerialStatus.SOLD || now == SerialStatus.DAMAGED)) {
      if (whId == null) throw new DataIntegrityViolationException("Serial has no warehouse to decrement from");
      inventory.adjust(new InventoryAdjustRequest(productId, whId, -1, "Serial status change to " + now));
    }
    // SOLD/DAMAGED → AVAILABLE : +1 (to current/assigned WH)
    if ((old == SerialStatus.SOLD || old == SerialStatus.DAMAGED) && now == SerialStatus.AVAILABLE) {
      if (whId == null) throw new DataIntegrityViolationException("Assign a warehouse when setting AVAILABLE");
      inventory.adjust(new InventoryAdjustRequest(productId, whId, 1, "Serial status change to AVAILABLE"));
    }
    // SOLD ↔ DAMAGED: no stock delta
  }

  @Override @Transactional(readOnly = true)
  public Page<SerialResponse> search(String q, Long productId, SerialStatus status, int page, int size) {
    Page<Serial> res = serials.search(
        productId, status, q == null ? "" : q.trim(),
        PageRequest.of(page, size, Sort.by("id").descending()));
    return res.map(this::toResponse);
  }

  @Override @Transactional(readOnly = true)
  public SerialVerifyResponse verify(String serialNumber) {
    return serials.findBySerialNumber(serialNumber)
        .map(s -> new SerialVerifyResponse(true, s.getId(), s.getProduct().getId(),
            s.getProduct().getSku(), s.getStatus(), s.getWarehouse() == null ? null : s.getWarehouse().getId()))
        .orElseGet(() -> new SerialVerifyResponse(false, null, null, null, null, null));
  }

  @Override @Transactional(readOnly = true)
  public List<SerialHistoryItem> history(Long serialId) {
    if (!serials.existsById(serialId)) throw new EntityNotFoundException("Serial not found");
    return history.findBySerial_IdOrderByAtDesc(serialId).stream()
        .map(h -> new SerialHistoryItem(h.getId(), h.getAt(), h.getAction(), h.getDetails()))
        .toList();
  }

  @Override
  public void addComplianceEvent(Long serialId, ComplianceEventRequest req) {
    Serial s = serials.findById(serialId)
        .orElseThrow(() -> new EntityNotFoundException("Serial not found"));
    complianceEvents.save(ComplianceEvent.builder()
        .serial(s).type(req.type()).payload(req.payload()).build());
    history.save(SerialHistory.builder()
        .serial(s).at(Instant.now()).action("COMPLIANCE_EVENT")
        .details(req.type()).build());
  }

  // ---------- mapping ----------
  private SerialResponse toResponse(Serial s) {
    Long whId = s.getWarehouse() == null ? null : s.getWarehouse().getId();
    String whCode = s.getWarehouse() == null ? null : s.getWarehouse().getCode();
    return new SerialResponse(
        s.getId(), s.getProduct().getId(), s.getProduct().getSku(), s.getProduct().getName(),
        s.getSerialNumber(), s.getStatus(), whId, whCode, s.getVersion(), s.getCreatedAt(), s.getUpdatedAt());
  }
}
