package com.project.posgunstore.Catalog.Controller;

import com.project.posgunstore.Catalog.DTO.*;
import com.project.posgunstore.Catalog.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService service;

  @PostMapping
  public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
  }

  @GetMapping
  public Page<ProductListItem> list(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size,
                                    @RequestParam(required = false) String sort) {
    return service.list(page, size, sort);
  }

  @GetMapping("/{id}")
  public ProductResponse get(@PathVariable Long id) { return service.get(id); }

  @PutMapping("/{id}")
  public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest req) {
    return service.update(id, req);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) { service.delete(id); }

  @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ProductImageResponse addImage(@PathVariable Long id,
                                       @RequestParam("file") MultipartFile file,
                                       @RequestParam(value = "category", required = false) String category) {
    return service.addImage(id, file, category);
  }

  @GetMapping("/search")
  public Page<ProductListItem> search(@RequestParam("q") String q,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
    return service.search(q, page, size);
  }

  @GetMapping("/barcode/{barcode}")
  public ProductResponse byBarcode(@PathVariable String barcode) {
    return service.getByBarcode(barcode);
  }
}
