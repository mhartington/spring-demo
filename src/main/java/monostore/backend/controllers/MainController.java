package monostore.backend.controllers;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

  @GetMapping("/")
  public Object gretting() {
    return ResponseEntity.ok(Map.of(
        "message", "E-commerce API is Running", "version", "1.0.0", "endpoints",
        Map.of("products", "/api/products", "users", "/api/users", "cart",
               "/api/cart", "orders", "/api/orders")));
  }
}
