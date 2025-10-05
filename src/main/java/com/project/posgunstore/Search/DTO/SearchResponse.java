// src/main/java/com/project/posgunstore/Search/DTO/SearchResponse.java
package com.project.posgunstore.Search.DTO;

import com.project.posgunstore.Catalog.DTO.ProductListItem;
import com.project.posgunstore.Serials.DTO.SerialResponse;

import java.util.List;

public record SearchResponse(
  List<ProductListItem> products,
  List<SerialResponse> serials,
  List<NamedItem> categories,
  List<NamedItem> manufacturers
) {}
