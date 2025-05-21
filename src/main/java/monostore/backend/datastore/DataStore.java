package monostore.backend.datastore;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import monostore.backend.models.Cart;
import monostore.backend.models.CustomUserDetails;
import monostore.backend.models.Order;
import monostore.backend.models.Product;

@Component
public class DataStore {
    public static final List<Product> products = Arrays.asList(
            new Product(
                    "1",
                    "Premium Wireless Headphones",
                    "High-quality wireless headphones with noise cancellation",
                    199.99,
                    "https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg",
                    "electronics",
                    45,
                    Instant.now(),
                    null),
            new Product(
                    "2",
                    "Smartphone 13 Pro",
                    "Latest smartphone with advanced camera features",
                    999.99,
                    "https://images.pexels.com/photos/404280/pexels-photo-404280.jpeg",
                    "electronics",
                    20,
                    Instant.now(),
                    null),
            new Product(
                    "3",
                    "Designer Watch",
                    "Elegant designer watch with leather strap",
                    299.99,
                    "https://images.pexels.com/photos/277390/pexels-photo-277390.jpeg",
                    "accessories",
                    15,
                    Instant.now(),
                    null),
            new Product(
                    "4",
                    "Premium Cotton T-Shirt",
                    "Soft and comfortable cotton t-shirt",
                    29.99,
                    "https://images.pexels.com/photos/5698851/pexels-photo-5698851.jpeg",
                    "clothing",
                    100,
                    Instant.now(),
                    null),
            new Product(
                    "5",
                    "Wireless Gaming Mouse",
                    "High-performance wireless gaming mouse",
                    79.99,
                    "https://images.pexels.com/photos/5082581/pexels-photo-5082581.jpeg",
                    "electronics",
                    30,
                    Instant.now(),
                    null));
    public static final Map<String, Cart> carts = new HashMap<>();
    public static final List<Order> orders = new ArrayList<>();
    public static final List<CustomUserDetails> users = Arrays.asList(
            new CustomUserDetails(
                    1L,
                    "john@example.com",
                    "password123",
                    Stream.of("ROLE_USER")
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())));
}
