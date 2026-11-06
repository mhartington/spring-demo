package monostore.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
  void adjustsStockAndRejectsInventoryBelowZero() {
    Product product = productService.getProductById("1");
    int initialStock = product.getStock();

    productService.updateProductStock("1", -2);
    assertEquals(initialStock - 2, product.getStock());

    assertThrows(IllegalArgumentException.class,
        () -> productService.updateProductStock("1", -initialStock));
    assertEquals(initialStock - 2, product.getStock());
  }

  @Test
  void supportsAddingProductsToTheCatalog() {
    Product added = new Product("new", "New product", "Description", 10.0,
        "image", "other", 3, null, null);

    productService.addProduct(added);

    assertEquals(added, productService.getProductById("new"));
  }
}
