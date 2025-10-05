package com.project.posgunstore.Catalog.Service.ServiceImpl;

import com.project.posgunstore.Catalog.DTO.*;
import com.project.posgunstore.Catalog.Model.Product;
import com.project.posgunstore.Catalog.Model.Category;
import com.project.posgunstore.Catalog.Model.Manufacturer;
import com.project.posgunstore.Catalog.Repository.*;
import com.project.posgunstore.Catalog.Service.ProductService;
import com.project.posgunstore.Storage.Model.ProductImage;
import com.project.posgunstore.Storage.Service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

  private final ProductRepository products;
  private final CategoryRepository categories;
  private final ManufacturerRepository manufacturers;
  private final FileStorageService fileStorage;

  @Override
  public ProductResponse create(ProductCreateRequest req) {
    if (products.existsBySku(req.sku()))
      throw new DataIntegrityViolationException("Duplicate SKU");
    if (req.barcode() != null && products.existsByBarcode(req.barcode()))
      throw new DataIntegrityViolationException("Duplicate barcode");

    Product p = new Product();
    applyCreate(req, p);
    Product saved = products.save(p);
    return toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductListItem> list(int page, int size, String sort) {
    Pageable pageable = PageRequest.of(page, size,
        (sort == null || sort.isBlank()) ? Sort.by("name").ascending() : Sort.by(sort));
    return products.findAllByIsActiveTrue(pageable).map(this::toListItem);
  }

  @Override
  @Transactional(readOnly = true)
  public ProductResponse get(Long id) {
    return products.findById(id).map(this::toResponse)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
  }

  @Override
  public ProductResponse update(Long id, ProductUpdateRequest req) {
    Product p = products.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    // optimistic lock
    if (!Objects.equals(p.getVersion(), req.version()))
      throw new DataIntegrityViolationException("Version conflict");

    // uniqueness checks if sku/barcode changed
    if (!p.getSku().equals(req.sku()) && products.existsBySku(req.sku()))
      throw new DataIntegrityViolationException("Duplicate SKU");
    if (req.barcode() != null && !req.barcode().equals(p.getBarcode())
        && products.existsByBarcode(req.barcode()))
      throw new DataIntegrityViolationException("Duplicate barcode");

    applyUpdate(req, p);
    return toResponse(products.save(p));
  }

  @Override
  public void delete(Long id) {
    if (!products.existsById(id)) throw new EntityNotFoundException("Product not found");
    products.deleteById(id); // switch to soft-delete if preferred
  }

  @Override
  public ProductImageResponse addImage(Long productId, MultipartFile file, String category) {
    Product p = products.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

    // reuse your existing storage service (uploads to Spaces/S3)
    ProductImage uploaded = fileStorage.uploadProductImage(file, category == null ? "products" : category);

    // set as primary image for product
    p.setImageUrl(uploaded.getUrl());
    products.save(p);

    return new ProductImageResponse(p.getId(), uploaded.getUrl());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductListItem> search(String q, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return products.search(q == null ? "" : q.trim(), pageable).map(this::toListItem);
  }

  @Override
  @Transactional(readOnly = true)
  public ProductResponse getByBarcode(String barcode) {
    return products.findByBarcode(barcode)
        .map(this::toResponse)
        .orElseThrow(() -> new EntityNotFoundException("Product with barcode not found"));
  }

  // ---------- mapping & apply helpers ----------

  private void applyCreate(ProductCreateRequest req, Product p) {
    p.setSku(req.sku());
    p.setName(req.name());
    p.setDescription(req.description());
    p.setBarcode(req.barcode());
    p.setCost(req.cost() == null ? p.getCost() : req.cost());
    p.setPrice(req.price() == null ? p.getPrice() : req.price());
    p.setIsSerialized(Boolean.TRUE.equals(req.isSerialized()));
    p.setIsActive(true);
    p.setImageUrl(req.imageUrl());

    if (req.categoryId() != null) {
      Category c = categories.findById(req.categoryId())
          .orElseThrow(() -> new EntityNotFoundException("Category not found"));
      p.setCategory(c);
    }
    if (req.manufacturerId() != null) {
      Manufacturer m = manufacturers.findById(req.manufacturerId())
          .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found"));
      p.setManufacturer(m);
    }
  }

  private void applyUpdate(ProductUpdateRequest req, Product p) {
    p.setSku(req.sku());
    p.setName(req.name());
    p.setDescription(req.description());
    p.setBarcode(req.barcode());
    p.setCost(req.cost() == null ? p.getCost() : req.cost());
    p.setPrice(req.price() == null ? p.getPrice() : req.price());
    if (req.isSerialized() != null) p.setIsSerialized(req.isSerialized());
    if (req.isActive() != null) p.setIsActive(req.isActive());
    if (req.imageUrl() != null) p.setImageUrl(req.imageUrl());

    if (req.categoryId() != null) {
      p.setCategory(categories.findById(req.categoryId())
          .orElseThrow(() -> new EntityNotFoundException("Category not found")));
    } else {
      p.setCategory(null);
    }

    if (req.manufacturerId() != null) {
      p.setManufacturer(manufacturers.findById(req.manufacturerId())
          .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found")));
    } else {
      p.setManufacturer(null);
    }
  }

  private ProductListItem toListItem(Product p) {
    String categoryName = p.getCategory() == null ? null : p.getCategory().getName();
    return new ProductListItem(
        p.getId(), p.getSku(), p.getName(), categoryName,
        p.getPrice(), p.getIsSerialized(), p.getIsActive(), p.getImageUrl());
  }

  private ProductResponse toResponse(Product p) {
    Long catId = p.getCategory() == null ? null : p.getCategory().getId();
    String catName = p.getCategory() == null ? null : p.getCategory().getName();
    Long manId = p.getManufacturer() == null ? null : p.getManufacturer().getId();
    String manName = p.getManufacturer() == null ? null : p.getManufacturer().getName();

    return new ProductResponse(
        p.getId(), p.getSku(), p.getName(), p.getDescription(), p.getBarcode(),
        catId, catName, manId, manName,
        p.getCost(), p.getPrice(), p.getIsSerialized(), p.getIsActive(),
        p.getImageUrl(), p.getVersion(), p.getCreatedAt(), p.getUpdatedAt());
  }
}
