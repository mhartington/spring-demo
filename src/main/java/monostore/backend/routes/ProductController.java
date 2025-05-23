package monostore.backend.routes;

import java.util.*;
import monostore.backend.datastore.DataStore;
import monostore.backend.models.Product;
import monostore.backend.service.ProductService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService; 
  public ProductController(ProductService productService) {
    this.productService = productService;
  }
  @GetMapping
  public Map<String, List<Product>>
  getAll(@RequestParam(required = false) String category,
         @RequestParam(required = false) String sort) {


    Map<String, List<Product>> results = new HashMap<>();
    if (category != null) {
      List<Product> filteredProducts = new ArrayList<>();
      for (Product product : productService.productStore) {
        if (product.getCategory().equalsIgnoreCase(category)) {
          filteredProducts.add(product);
        }
      }
      results.put("products", filteredProducts);
      return results;
    }
    
    results.put("products", productService.productStore);
    return results;
  }

  @GetMapping("/{id}")
  public HttpEntity<?> getById(@PathVariable String id) { 
    Product product = productService.getProductById(id);
    if (product != null) {
      return ResponseEntity.ok(Map.of("product", product));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Product not found"));
    }
  }
  //
  //    @PostMapping
  //    public ResponseEntity<Product> create(@Valid @RequestBody Product input)
  //    {
  //        input.setId(UUID.randomUUID().toString());
  //        input.setCreatedAt(java.time.Instant.now());
  //        ds.products.add(input);
  //        return ResponseEntity.ok(input);
  //    }
  //
  //    @PutMapping("/{id}")
  //    public ResponseEntity<Product> update(@PathVariable String id,
  //                                          @Valid @RequestBody Product input)
  //                                          {
  //        return ds.products.stream()
  //            .filter(p -> p.getId().equals(id))
  //            .findFirst()
  //            .map(p -> {
  //                input.setId(p.getId());
  //                input.setUpdatedAt(java.time.Instant.now());
  //                ds.products.remove(p);
  //                ds.products.add(input);
  //                return ResponseEntity.ok(input);
  //            })
  //            .orElse(ResponseEntity.notFound().build());
  //    }
  //
  //    @DeleteMapping("/{id}")
  //    public ResponseEntity<?> delete(@PathVariable String id) {
  //        boolean removed = ds.products.removeIf(p -> p.getId().equals(id));
  //        if (!removed) {
  //            return ResponseEntity.notFound().build();
  //        }
  //        return ResponseEntity.ok().body(Map.of("message", "Product
  //        deleted"));
  //    }
}
