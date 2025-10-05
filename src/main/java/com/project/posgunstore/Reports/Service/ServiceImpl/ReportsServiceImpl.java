// src/main/java/com/project/posgunstore/Reports/Service/ServiceImpl/ReportsServiceImpl.java
package com.project.posgunstore.Reports.Service.ServiceImpl;

import com.project.posgunstore.Reports.DTO.*;
import com.project.posgunstore.Reports.Repository.ReportsRepository;
import com.project.posgunstore.Reports.Service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportsServiceImpl implements ReportsService {

  private final ReportsRepository repo;

  @Override
  public InventoryValuationResponse inventoryValuation() {
    List<InventoryValuationRow> rows = repo.inventoryValuation();
    BigDecimal total = rows.stream()
        .map(InventoryValuationRow::getValue)
        .reduce(BigDecimal.ZERO, (a,b) -> a.add(b == null ? BigDecimal.ZERO : b));
    return new InventoryValuationResponse(rows, total);
  }

  @Override
  public Page<StockLevelRow> stockLevels(Long productId, Long warehouseId, int page, int size) {
    return repo.stockLevels(productId, warehouseId, PageRequest.of(page, size));
  }

  @Override
  public Page<StockMovementRow> stockMovements(Long productId, Instant from, Instant to, int page, int size) {
    return repo.stockMovements(productId, from, to, PageRequest.of(page, size));
  }

  @Override
  public List<SerialsStatusRow> serialsStatus() {
    return repo.serialsStatus();
  }
}
