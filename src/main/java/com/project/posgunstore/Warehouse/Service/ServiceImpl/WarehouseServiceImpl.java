// WarehouseServiceImpl.java
package com.project.posgunstore.Warehouse.Service.ServiceImpl;

import com.project.posgunstore.Inventory.Repository.InventoryLevelRepository;
import com.project.posgunstore.Warehouse.DTO.*;
import com.project.posgunstore.Warehouse.Model.Warehouse;
import com.project.posgunstore.Warehouse.Repository.WarehouseRepository;
import com.project.posgunstore.Warehouse.Service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

  private final WarehouseRepository warehouses;
  private final InventoryLevelRepository levels;

  @Override
  public WarehouseResponse create(WarehouseCreateRequest req) {
    if (warehouses.existsByCodeIgnoreCase(req.code()))
      throw new DataIntegrityViolationException("Duplicate warehouse code");

    Warehouse w = Warehouse.builder()
        .name(req.name()).code(req.code())
        .address(req.address())
        .active(req.active() == null ? true : req.active())
        .build();
    return toResponse(warehouses.save(w));
  }

  @Override @Transactional(readOnly = true)
  public Page<WarehouseResponse> list(int page, int size) {
    return warehouses.findAll(PageRequest.of(page, size, Sort.by("name"))).map(this::toResponse);
  }

  @Override @Transactional(readOnly = true)
  public WarehouseResponse get(Long id) {
    return warehouses.findById(id).map(this::toResponse)
        .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
  }

  @Override
  public WarehouseResponse update(Long id, WarehouseUpdateRequest req) {
    Warehouse w = warehouses.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

    if (!w.getCode().equalsIgnoreCase(req.code()) && warehouses.existsByCodeIgnoreCase(req.code()))
      throw new DataIntegrityViolationException("Duplicate warehouse code");

    if (!w.getVersion().equals(req.version()))
      throw new DataIntegrityViolationException("Version conflict");

    w.setName(req.name());
    w.setCode(req.code());
    w.setAddress(req.address());
    w.setActive(req.active());
    return toResponse(warehouses.save(w));
  }

  @Override
  public void delete(Long id) {
    Warehouse w = warehouses.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

    long inUse = levels.countByWarehouse_Id(id);
    if (inUse > 0) {
      throw new DataIntegrityViolationException("Cannot delete: inventory exists in this warehouse");
    }
    warehouses.delete(w);
  }

  private WarehouseResponse toResponse(Warehouse w) {
    return new WarehouseResponse(w.getId(), w.getName(), w.getCode(), w.getAddress(),
        w.getActive(), w.getVersion(), w.getCreatedAt(), w.getUpdatedAt());
  }
}
