package monostore.backend.service;

import monostore.backend.models.Product;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

  // Simulating an in-memory datastore for products
  public final List<Product> productStore = Arrays.asList(
    new Product( "1", "Premium Wireless Headphones", "High-quality wireless headphones with noise cancellation", 199.99, "https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg", "electronics", 45, Instant.now(), null),
    new Product( "2", "Smartphone 13 Pro", "Latest smartphone with advanced camera features", 999.99, "https://images.pexels.com/photos/404280/pexels-photo-404280.jpeg", "electronics", 20, Instant.now(), null),
    new Product( "3", "Designer Watch", "Elegant designer watch with leather strap", 299.99, "https://images.pexels.com/photos/277390/pexels-photo-277390.jpeg", "accessories", 15, Instant.now(), null),
    new Product( "4", "Premium Cotton T-Shirt", "Soft and comfortable cotton t-shirt", 29.99, "https://images.pexels.com/photos/5698851/pexels-photo-5698851.jpeg", "clothing", 100, Instant.now(), null),
    new Product( "5", "Wireless Gaming Mouse", "High-performance wireless gaming mouse", 79.99, "https://images.pexels.com/photos/5082581/pexels-photo-5082581.jpeg", "electronics", 30, Instant.now(), null)
  );

  /**
   * Updates the stock of a product by the given adjustment amount.
   *
   * @param productId       The ID of the product.
   * @param adjustmentValue The amount to adjust the product's stock by.
   */
  public void updateProductStock(String productId, int adjustmentValue) {
    Optional<Product> productOptional = productStore.stream()
      .filter(product -> product.getId().equals(productId))
      .findFirst();

    if (productOptional.isPresent()) {
      Product product = productOptional.get();
      int newStock = product.getStock() + adjustmentValue;

      if (newStock < 0) {
        throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
      }

      product.setStock(newStock);
    } else {
      throw new IllegalArgumentException("Product not found: " + productId);
    }
  }

  /**
   * Fetches a product by its ID.
   *
   * @param productId The ID of the product.
   * @return An Product containing the product if found, otherwise empty.
   */
  public Product getProductById(String productId) {
    return productStore.stream()
      .filter(product -> product.getId().equals(productId))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
  }

  /**
   * Adds a product to the product store (for initialization or testing).
   *
   * @param product The product to add.
   */
  public void addProduct(Product product) {
    productStore.add(product);
  }
}
