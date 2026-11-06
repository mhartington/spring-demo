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
  private Product product;

  @BeforeEach
  void setUp() {
    productService = new ProductService();
    cartService = new CartService(productService);
    product = productService.getProductById("1");
  }

  @Test
  void combinesItemsAndRecalculatesTheCartTotal() {
    Cart cart = cartService.addItemToCart("user-1", product, 2);
    cart = cartService.addItemToCart("user-1", product, 1);

    assertEquals(1, cart.getItems().size());
    assertEquals(3, cart.getItems().get(0).getQuantity());
    assertEquals(599.97, cart.getTotal(), 0.001);
  }

  @Test
  void rejectsInvalidAndOverstockQuantitiesWithoutChangingTheCart() {
    Cart cart = cartService.addItemToCart("user-1", product, 2);

    assertThrows(IllegalArgumentException.class,
        () -> cartService.addItemToCart("user-1", product, 0));
    assertThrows(IllegalArgumentException.class,
        () -> cartService.addItemToCart("user-1", product, product.getStock()));
    assertThrows(IllegalArgumentException.class,
        () -> cartService.updateItemInCart("user-1", product.getId(), 0));
    assertThrows(IllegalArgumentException.class,
        () -> cartService.updateItemInCart(
            "user-1", product.getId(), product.getStock() + 1));

    assertEquals(2, cart.getItems().get(0).getQuantity());
    assertEquals(399.98, cart.getTotal(), 0.001);
  }

  @Test
  void updatesAndRemovesItemsWhileMaintainingTheTotal() {
    cartService.addItemToCart("user-1", product, 2);
    Cart updated = cartService.updateItemInCart("user-1", product.getId(), 1);

    assertEquals(199.99, updated.getTotal(), 0.001);
    Cart removed = cartService.removeItemFromCart("user-1", product.getId());

    assertEquals(0, removed.getItems().size());
    assertEquals(0.0, removed.getTotal(), 0.001);
  }
}
