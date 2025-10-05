package com.project.posgunstore.Catalog.Service;

import com.project.posgunstore.Catalog.DTO.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
  ProductResponse create(ProductCreateRequest req);
  Page<ProductListItem> list(int page, int size, String sort);
  ProductResponse get(Long id);
  ProductResponse update(Long id, ProductUpdateRequest req);
  void delete(Long id); // soft or hard—here we do hard delete by default
  ProductImageResponse addImage(Long productId, MultipartFile file, String category);
  Page<ProductListItem> search(String q, int page, int size);
  ProductResponse getByBarcode(String barcode);
}
