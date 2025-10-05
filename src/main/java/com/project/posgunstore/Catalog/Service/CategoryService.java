// CategoryService.java
package com.project.posgunstore.Catalog.Service;

import com.project.posgunstore.Catalog.DTO.*;
import org.springframework.data.domain.Page;

public interface CategoryService {
  CategoryResponse create(CategoryCreateRequest req);
  Page<CategoryResponse> list(int page, int size, String q);
  CategoryResponse get(Long id);
  CategoryResponse update(Long id, CategoryUpdateRequest req);
  void delete(Long id);
}
