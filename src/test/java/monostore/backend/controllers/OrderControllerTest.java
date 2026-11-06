package monostore.backend.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import monostore.backend.models.Cart;
import monostore.backend.models.CartItem;
import monostore.backend.models.CustomUserDetails;
import monostore.backend.models.Order;
import monostore.backend.models.OrderRequest;
import monostore.backend.models.Product;
import monostore.backend.service.CartService;
import monostore.backend.service.OrderService;
import monostore.backend.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class OrderControllerTest {

  private ProductService productService;
  private CartService cartService;
  private OrderService orderService;
  private OrderController orderController;
  private Product product;

  @BeforeEach
  void setUp() {
    productService = new ProductService();
    cartService = new CartService(productService);
    orderService = new OrderService();
    orderController = new OrderController(orderService, cartService, productService);
    product = productService.getProductById("1");

    CustomUserDetails user = new CustomUserDetails(
        1L, "user@example.com", "password",
        List.of(new SimpleGrantedAuthority("ROLE_USER")));
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void checkoutDeductsStockAndCancellationRestoresIt() {
    cartService.addItemToCart("1", product, 2);
    int startingStock = product.getStock();

    ResponseEntity<?> checkout = orderController.createOrder(new OrderRequest());

    assertEquals(HttpStatus.CREATED, checkout.getStatusCode());
    Order order = (Order) checkout.getBody();
    assertNotNull(order);
    assertEquals(startingStock - 2, product.getStock());
    assertEquals(0, cartService.getCartByUserId("1").getItems().size());

    ResponseEntity<?> cancellation = orderController.cancelOrder(order.getId());

    assertEquals(HttpStatus.OK, cancellation.getStatusCode());
    assertEquals("cancelled", order.getStatus());
    assertEquals(startingStock, product.getStock());
  }

  @Test
  void insufficientStockDoesNotCreateAnOrderOrClearTheCart() {
    Cart cart = cartService.addItemToCart("1", product, 1);
    cart.getItems().get(0).setQuantity(product.getStock() + 1);
    cart.setTotal(product.getPrice() * (product.getStock() + 1));
    int startingStock = product.getStock();

    ResponseEntity<?> checkout = orderController.createOrder(new OrderRequest());

    assertEquals(HttpStatus.BAD_REQUEST, checkout.getStatusCode());
    assertEquals(0, orderService.getOrdersByUserId("1").size());
    assertEquals(startingStock, product.getStock());
    assertEquals(1, cartService.getCartByUserId("1").getItems().size());
  }

  @Test
  void orderCannotBeCancelledByAnotherUser() {
    cartService.addItemToCart("1", product, 1);
    Order order = (Order) orderController.createOrder(new OrderRequest()).getBody();

    CustomUserDetails anotherUser = new CustomUserDetails(
        2L, "other@example.com", "password",
        List.of(new SimpleGrantedAuthority("ROLE_USER")));
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(
            anotherUser, null, anotherUser.getAuthorities()));

    ResponseEntity<?> cancellation = orderController.cancelOrder(order.getId());

    assertEquals(HttpStatus.NOT_FOUND, cancellation.getStatusCode());
  }
}
