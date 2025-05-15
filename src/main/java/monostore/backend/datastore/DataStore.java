package monostore.backend.datastore;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import monostore.backend.models.Cart;
import monostore.backend.models.Order;
import monostore.backend.models.Product;
import monostore.backend.models.User;

@Component
public class DataStore {
  public final List<Product> products = new ArrayList<>();
  public final Map<String, User> users = new HashMap<>();
  public final Map<String, Cart> carts = new HashMap<>();
  public final List<Order> orders = new ArrayList<>();

  @PostConstruct
  public void init() {
    // sample product
    products.add(new Product(
      "1",
      "Premium Wireless Headphones",
      "High-quality wireless headphones with noise cancellation",
      199.99,
      "https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg",
      "electronics",
      50,
      Instant.now(),
      null));
    products.add(new Product(
      "2",
      "Smartphone 13 Pro",
      "Latest smartphone with advanced camera features",
      999.99,
      "https://images.pexels.com/photos/404280/pexels-photo-404280.jpeg",
      "electronics",
      20,
      Instant.now(),
      null
    ));
    // TODO: add more sample data or load from JSON
  }
}
