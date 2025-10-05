// src/main/java/com/project/posgunstore/Search/Service/ServiceImpl/SearchServiceImpl.java
package com.project.posgunstore.Search.Service.ServiceImpl;

import com.project.posgunstore.Catalog.DTO.ProductListItem;
import com.project.posgunstore.Catalog.Model.Product;
import com.project.posgunstore.Catalog.Repository.*;
import com.project.posgunstore.Search.DTO.*;
import com.project.posgunstore.Search.Service.SearchService;
import com.project.posgunstore.Serials.DTO.SerialResponse;
import com.project.posgunstore.Serials.Model.Serial;
import com.project.posgunstore.Serials.Repository.SerialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchServiceImpl implements SearchService {

  private final ProductRepository products;
  private final SerialRepository serials;
  private final CategoryRepository categories;
  private final ManufacturerRepository manufacturers;

  @Override
  public SearchResponse search(String q, SearchScope scope, int limit) {
    int size = Math.min(Math.max(limit, 1), 50);
    var pageable = PageRequest.of(0, size);

    List<ProductListItem> prod = List.of();
    List<SerialResponse> ser = List.of();
    List<NamedItem> cats = List.of();
    List<NamedItem> mans = List.of();

    if (scope == SearchScope.ALL || scope == SearchScope.PRODUCTS) {
      prod = products.search(q == null ? "" : q.trim(), pageable)
          .map(this::toListItem).getContent();
    }
    if (scope == SearchScope.ALL || scope == SearchScope.SERIALS) {
      ser = serials.search(null, null, q == null ? "" : q.trim(), pageable)
          .map(this::toSerialItem).getContent();
    }
    if (scope == SearchScope.ALL || scope == SearchScope.CATEGORIES) {
      cats = categories.findByNameContainingIgnoreCase(q == null ? "" : q.trim(), pageable)
          .map(c -> new NamedItem(c.getId(), c.getName())).getContent();
    }
    if (scope == SearchScope.ALL || scope == SearchScope.MANUFACTURERS) {
      mans = manufacturers.findByNameContainingIgnoreCase(q == null ? "" : q.trim(), pageable)
          .map(m -> new NamedItem(m.getId(), m.getName())).getContent();
    }
    return new SearchResponse(prod, ser, cats, mans);
  }

  private ProductListItem toListItem(Product p) {
    String categoryName = p.getCategory() == null ? null : p.getCategory().getName();
    return new ProductListItem(
        p.getId(), p.getSku(), p.getName(), categoryName,
        p.getPrice(), p.getIsSerialized(), p.getIsActive(), p.getImageUrl());
  }

  private SerialResponse toSerialItem(Serial s) {
    Long whId = s.getWarehouse() == null ? null : s.getWarehouse().getId();
    String whCode = s.getWarehouse() == null ? null : s.getWarehouse().getCode();
    return new SerialResponse(
        s.getId(), s.getProduct().getId(), s.getProduct().getSku(), s.getProduct().getName(),
        s.getSerialNumber(), s.getStatus(), whId, whCode, s.getVersion(), s.getCreatedAt(), s.getUpdatedAt());
  }
}
