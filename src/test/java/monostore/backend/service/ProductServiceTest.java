package monostore.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import monostore.backend.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductServiceTest {

  private ProductService productService;

  @BeforeEach
  void setUp() {
    productService = new ProductService();
  }

  @Test
  void updatesStockAndRejectsAnAdjustmentBelowZero() {
    Product product = productService.getProductById("1");

    productService.updateProductStock("1", -2);
    assertEquals(43, product.getStock());

    assertThrows(IllegalArgumentException.class,
        () -> productService.updateProductStock("1", -44));
    assertEquals(43, product.getStock());
  }

  @Test
  void rejectsUnknownProductsAndAllowsAddingProducts() {
    assertThrows(IllegalArgumentException.class,
        () -> productService.getProductById("missing"));

    Product added = new Product(
        "6", "Desk Lamp", "Adjustable desk lamp", 49.99, "image", "home", 8,
        Instant.now(), null);
    productService.addProduct(added);

    assertEquals(added, productService.getProductById("6"));
  }
}
