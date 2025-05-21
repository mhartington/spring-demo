package monostore.backend.routes;

import java.util.*;
import monostore.backend.datastore.DataStore;
import monostore.backend.models.Product;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  @GetMapping
  public Map<String, List<Product>>
  getAll(@RequestParam(required = false) String category,
         @RequestParam(required = false) String sort) {


    Map<String, List<Product>> results = new HashMap<>();
    if (category != null) {
      List<Product> filteredProducts = new ArrayList<>();
      for (Product product : DataStore.products) {
        if (product.getCategory().equalsIgnoreCase(category)) {
          filteredProducts.add(product);
        }
      }
      results.put("products", filteredProducts);
      return results;
    }
    
    results.put("products", DataStore.products);
    return results;
  }

  @GetMapping("/{id}")
  public HttpEntity<?> getById(@PathVariable String id) {
    return DataStore.products.stream()
        .filter(p -> p.getId().equals(id))
        .findFirst()
        .<ResponseEntity<?>>map(p -> ResponseEntity.ok(Collections.singletonMap("product", p)))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND) .body(Collections.singletonMap( "error", "Product not found")));
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
