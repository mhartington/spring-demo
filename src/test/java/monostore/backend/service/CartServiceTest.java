package monostore.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import monostore.backend.models.Cart;
import monostore.backend.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartServiceTest {
  private ProductService products;
  private CartService carts;

  @BeforeEach
  void setUp() {
    products = new ProductService();
    carts = new CartService(products);
  }

  @Test
  void addsItemsAndCalculatesTotal() {
    Product product = products.getProductById("1");
    Cart cart = carts.addItemToCart("user", product, 2);

    assertThat(cart.getItems()).singleElement().satisfies(item -> {
      assertThat(item.getProduct().getId()).isEqualTo("1");
      assertThat(item.getQuantity()).isEqualTo(2);
    });
    assertThat(cart.getTotal()).isEqualTo(399.98);
  }

  @Test
  void rejectsInvalidAndAccumulatedOutOfStockQuantities() {
    Product product = products.getProductById("3");
    assertThatThrownBy(() -> carts.addItemToCart("user", product, 0))
        .isInstanceOf(IllegalArgumentException.class);
    carts.addItemToCart("user", product, 10);
    assertThatThrownBy(() -> carts.addItemToCart("user", product, 6))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Not enough stock available");
  }

  @Test
  void updatingAnUninitializedCartReturnsDomainError() {
    assertThatThrownBy(() -> carts.updateItemInCart("new-user", "1", 1))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Item not found in cart");
  }
}
