package monostore.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import monostore.backend.models.Cart;
import monostore.backend.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartServiceTest {

  private ProductService productService;
  private CartService cartService;

  @BeforeEach
  void setUp() {
    productService = new ProductService();
    cartService = new CartService(productService);
  }

  @Test
  void addingSameProductAccumulatesQuantityAndRecalculatesTotal() {
    Product product = productService.getProductById("1");

    Cart cart = cartService.addItemToCart("user-1", product, 2);
    cart = cartService.addItemToCart("user-1", product, 3);

    assertEquals(1, cart.getItems().size());
    assertEquals(5, cart.getItems().get(0).getQuantity());
    assertEquals(product.getPrice() * 5, cart.getTotal(), 0.001);
  }

  @Test
  void rejectsInvalidOrOverstockQuantityWithoutChangingCart() {
    Product product = productService.getProductById("1");
    Cart cart = cartService.addItemToCart("user-1", product, product.getStock());

    assertThrows(IllegalArgumentException.class,
        () -> cartService.addItemToCart("user-1", product, 1));
    assertThrows(IllegalArgumentException.class,
        () -> cartService.addItemToCart("user-2", product, 0));

    assertEquals(product.getStock(), cart.getItems().get(0).getQuantity());
    assertEquals(product.getPrice() * product.getStock(), cart.getTotal(), 0.001);
  }

  @Test
  void rejectsUpdatingAnItemInAnUninitializedCart() {
    assertThrows(IllegalArgumentException.class,
        () -> cartService.updateItemInCart("user-1", "1", 1));
  }
}
