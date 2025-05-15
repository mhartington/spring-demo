package monostore.backend.routes;

import monostore.backend.datastore.DataStore;
import monostore.backend.models.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private DataStore ds;
//  ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public Map<String, List<Product>> getAll(@RequestParam(required=false) String category, @RequestParam(required=false) String sort)  {
        // TODO: implement filtering and sorting
      Map <String, List<Product>> results = new HashMap<>();
      results.put("products", ds.products);
//      results.entrySet().stream()
      return results;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable String id) {
        return ds.products.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product input) {
        input.setId(UUID.randomUUID().toString());
        input.setCreatedAt(java.time.Instant.now());
        ds.products.add(input);
        return ResponseEntity.ok(input);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable String id,
                                          @Valid @RequestBody Product input) {
        return ds.products.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .map(p -> {
                input.setId(p.getId());
                input.setUpdatedAt(java.time.Instant.now());
                ds.products.remove(p);
                ds.products.add(input);
                return ResponseEntity.ok(input);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        boolean removed = ds.products.removeIf(p -> p.getId().equals(id));
        if (!removed) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(Map.of("message", "Product deleted"));
    }
}
