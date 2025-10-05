// ReportsRepository.java
package com.project.posgunstore.Reports.Repository;

import com.project.posgunstore.Reports.DTO.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ReportsRepository extends JpaRepository<com.project.posgunstore.Catalog.Model.Product, Long> {

  // Inventory valuation
  @Query("""
     select p.id as productId,
            p.sku as sku,
            p.name as name,
            coalesce(sum(il.quantity),0) as quantity,
            p.cost as cost,
            coalesce(sum(il.quantity),0) * p.cost as value
     from Product p
     left join com.project.posgunstore.Inventory.Model.InventoryLevel il
            on il.product = p
     group by p.id, p.sku, p.name, p.cost
     order by p.name
     """)
  List<InventoryValuationRow> inventoryValuation();

  // Stock levels (optionally filtered)
  @Query("""
     select il.product.id as productId,
            p.sku as sku,
            p.name as productName,
            il.warehouse.id as warehouseId,
            w.code as warehouseCode,
            il.quantity as quantity,
            il.reorderPoint as reorderPoint
     from com.project.posgunstore.Inventory.Model.InventoryLevel il
     join il.product p
     join il.warehouse w
     where (:productId is null or p.id = :productId)
       and (:warehouseId is null or w.id = :warehouseId)
     order by p.name, w.code
     """)
  Page<StockLevelRow> stockLevels(@Param("productId") Long productId,
                                  @Param("warehouseId") Long warehouseId,
                                  Pageable pageable);

  // Stock movements (UNION: adjustments + transfers) — native for union support
  @Query(value = """
     (select
         'ADJUSTMENT' as type,
         p.id as productId,
         p.sku as sku,
         p.name as productName,
         null as fromWarehouseCode,
         null as toWarehouseCode,
         w.code as warehouseCode,
         a.delta as quantity,
         a.reason as reason,
         a.created_at as createdAt
       from inventory_adjustments a
       join products p on p.id = a.product_id
       join warehouses w on w.id = a.warehouse_id
       where (:productId is null or p.id = :productId)
         and (:fromTs is null or a.created_at >= :fromTs)
         and (:toTs is null or a.created_at < :toTs)
     )
     union all
     (select
         'TRANSFER' as type,
         p.id as productId,
         p.sku as sku,
         p.name as productName,
         wfrom.code as fromWarehouseCode,
         wto.code as toWarehouseCode,
         null as warehouseCode,
         m.quantity as quantity,
         null as reason,
         m.created_at as createdAt
       from inventory_movements m
       join products p on p.id = m.product_id
       join warehouses wfrom on wfrom.id = m.from_wh_id
       join warehouses wto   on wto.id   = m.to_wh_id
       where (:productId is null or p.id = :productId)
         and (:fromTs is null or m.created_at >= :fromTs)
         and (:toTs is null or m.created_at < :toTs)
     )
     order by createdAt desc
     """,
     countQuery = """
     select count(*) from (
       (select 1
        from inventory_adjustments a
        join products p on p.id = a.product_id
        where (:productId is null or p.id = :productId)
          and (:fromTs is null or a.created_at >= :fromTs)
          and (:toTs is null or a.created_at < :toTs))
       union all
       (select 1
        from inventory_movements m
        join products p on p.id = m.product_id
        where (:productId is null or p.id = :productId)
          and (:fromTs is null or m.created_at >= :fromTs)
          and (:toTs is null or m.created_at < :toTs))
     ) t
     """,
     nativeQuery = true)
  Page<StockMovementRow> stockMovements(@Param("productId") Long productId,
                                        @Param("fromTs") Instant fromTs,
                                        @Param("toTs") Instant toTs,
                                        Pageable pageable);

  // Serials status (counts per product)
  @Query("""
     select p.id as productId,
            p.sku as sku,
            p.name as productName,
            sum(case when s.status = com.project.posgunstore.Serials.Model.SerialStatus.AVAILABLE then 1 else 0 end) as available,
            sum(case when s.status = com.project.posgunstore.Serials.Model.SerialStatus.SOLD then 1 else 0 end) as sold,
            sum(case when s.status = com.project.posgunstore.Serials.Model.SerialStatus.DAMAGED then 1 else 0 end) as damaged
     from com.project.posgunstore.Serials.Model.Serial s
     join s.product p
     group by p.id, p.sku, p.name
     order by p.name
     """)
  List<SerialsStatusRow> serialsStatus();
}
