// CategoryServiceImpl.java
package com.project.posgunstore.Catalog.Service.ServiceImpl;

import com.project.posgunstore.Catalog.DTO.*;
import com.project.posgunstore.Catalog.Model.Category;
import com.project.posgunstore.Catalog.Repository.CategoryRepository;
import com.project.posgunstore.Catalog.Repository.ProductRepository;
import com.project.posgunstore.Catalog.Service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categories;
  private final ProductRepository products;

  @Override
  public CategoryResponse create(CategoryCreateRequest req) {
    if (categories.existsByNameIgnoreCase(req.name()))
      throw new DataIntegrityViolationException("Duplicate category name");

    Category c = Category.builder().name(req.name()).build();
    return toResponse(categories.save(c));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CategoryResponse> list(int page, int size, String q) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
    Page<Category> p = (q == null || q.isBlank())
        ? categories.findAll(pageable)
        : categories.findByNameContainingIgnoreCase(q.trim(), pageable);
    return p.map(this::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public CategoryResponse get(Long id) {
    return categories.findById(id).map(this::toResponse)
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));
  }

  @Override
  public CategoryResponse update(Long id, CategoryUpdateRequest req) {
    Category c = categories.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    if (!c.getName().equalsIgnoreCase(req.name()) &&
        categories.existsByNameIgnoreCase(req.name())) {
      throw new DataIntegrityViolationException("Duplicate category name");
    }

    // optimistic lock
    if (!c.getVersion().equals(req.version()))
      throw new DataIntegrityViolationException("Version conflict");

    c.setName(req.name());
    return toResponse(categories.save(c));
  }

  @Override
  public void delete(Long id) {
    Category c = categories.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    long inUse = products.countByCategory_Id(id);
    if (inUse > 0) {
      throw new DataIntegrityViolationException("Cannot delete: category in use by " + inUse + " product(s)");
    }
    categories.delete(c);
  }

  private CategoryResponse toResponse(Category c) {
    return new CategoryResponse(c.getId(), c.getName(), c.getVersion(), c.getCreatedAt(), c.getUpdatedAt());
  }
}
