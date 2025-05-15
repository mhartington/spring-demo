package monostore.datastore;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import monostore.models.Cart;
import monostore.models.Order;
import monostore.models.Product;
import monostore.models.User;

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
            "", 
            "electronics", 
            50,
            Instant.now(), 
            null));
        // TODO: add more sample data or load from JSON
    }
}
