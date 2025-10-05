// ManufacturerServiceImpl.java
package com.project.posgunstore.Catalog.Service.ServiceImpl;

import com.project.posgunstore.Catalog.DTO.*;
import com.project.posgunstore.Catalog.Model.Manufacturer;
import com.project.posgunstore.Catalog.Repository.ManufacturerRepository;
import com.project.posgunstore.Catalog.Repository.ProductRepository;
import com.project.posgunstore.Catalog.Service.ManufacturerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ManufacturerServiceImpl implements ManufacturerService {

  private final ManufacturerRepository manufacturers;
  private final ProductRepository products;

  @Override
  public ManufacturerResponse create(ManufacturerCreateRequest req) {
    if (manufacturers.existsByNameIgnoreCase(req.name()))
      throw new DataIntegrityViolationException("Duplicate manufacturer name");

    Manufacturer m = Manufacturer.builder().name(req.name()).build();
    return toResponse(manufacturers.save(m));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ManufacturerResponse> list(int page, int size, String q) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
    Page<Manufacturer> p = (q == null || q.isBlank())
        ? manufacturers.findAll(pageable)
        : manufacturers.findByNameContainingIgnoreCase(q.trim(), pageable);
    return p.map(this::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public ManufacturerResponse get(Long id) {
    return manufacturers.findById(id).map(this::toResponse)
        .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found"));
  }

  @Override
  public ManufacturerResponse update(Long id, ManufacturerUpdateRequest req) {
    Manufacturer m = manufacturers.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found"));

    if (!m.getName().equalsIgnoreCase(req.name()) &&
        manufacturers.existsByNameIgnoreCase(req.name())) {
      throw new DataIntegrityViolationException("Duplicate manufacturer name");
    }

    // optimistic lock
    if (!m.getVersion().equals(req.version()))
      throw new DataIntegrityViolationException("Version conflict");

    m.setName(req.name());
    return toResponse(manufacturers.save(m));
  }

  @Override
  public void delete(Long id) {
    Manufacturer m = manufacturers.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found"));

    long inUse = products.countByManufacturer_Id(id);
    if (inUse > 0) {
      throw new DataIntegrityViolationException("Cannot delete: manufacturer in use by " + inUse + " product(s)");
    }
    manufacturers.delete(m);
  }

  private ManufacturerResponse toResponse(Manufacturer m) {
    return new ManufacturerResponse(m.getId(), m.getName(), m.getVersion(), m.getCreatedAt(), m.getUpdatedAt());
  }
}
