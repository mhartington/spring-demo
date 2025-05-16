package monostore.backend.datastore;

import java.time.Instant;
import java.util.*;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import monostore.backend.models.Cart;
import monostore.backend.models.Order;
import monostore.backend.models.Product;
import monostore.backend.models.User;

@Component
public class DataStore {
  public final List<Product> products = Arrays.asList(
    new Product(
      "1",
      "Premium Wireless Headphones",
      "High-quality wireless headphones with noise cancellation",
      199.99,
      "https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg",
      "electronics",
      45,
      Instant.now(),
      null
    ),
    new Product(
      "2",
      "Smartphone 13 Pro",
      "Latest smartphone with advanced camera features",
      999.99,
      "https://images.pexels.com/photos/404280/pexels-photo-404280.jpeg",
      "electronics",
      20,
      Instant.now(),
      null
    ),
    new Product(
      "3",
      "Designer Watch",
      "Elegant designer watch with leather strap",
      299.99,
      "https://images.pexels.com/photos/277390/pexels-photo-277390.jpeg",
      "accessories",
      15,
      Instant.now(),
      null
    ),
    new Product(
      "4",
      "Premium Cotton T-Shirt",
      "Soft and comfortable cotton t-shirt",
      29.99,
      "https://images.pexels.com/photos/5698851/pexels-photo-5698851.jpeg",
      "clothing",
      100,
      Instant.now(),
      null
    ),
    new Product(
      "5",
      "Wireless Gaming Mouse",
      "High-performance wireless gaming mouse",
      79.99,
      "https://images.pexels.com/photos/5082581/pexels-photo-5082581.jpeg",
      "electronics",
      30,
      Instant.now(),
      null
    )
  );
  public final Map<String, User> users = new HashMap<>();
  public final Map<String, Cart> carts = new HashMap<>();
  public final List<Order> orders = new ArrayList<>();

//  @PostConstruct
//  public void init()  {
//  }
}
