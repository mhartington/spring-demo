package monostore.backend.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import monostore.backend.models.Cart;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class OrderControllerTest {

  private static final String USER_ID = "1";

  private ProductService productService;
  private CartService cartService;
  private OrderService orderService;
  private OrderController orderController;

  @BeforeEach
  void setUp() {
    productService = new ProductService();
    cartService = new CartService(productService);
    orderService = new OrderService();
    orderController = new OrderController(orderService, cartService, productService);

    CustomUserDetails user = new CustomUserDetails(1L, "user@example.com", "password",
        java.util.List.of(new SimpleGrantedAuthority("ROLE_USER")));
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void checkoutRejectsStaleCartWithoutCreatingOrderOrClearingCart() {
    Product product = productService.getProductById("1");
    cartService.addItemToCart(USER_ID, product, 2);
    productService.updateProductStock("1", -44);

    ResponseEntity<?> response = orderController.createOrder(validOrderRequest());

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(orderService.getOrdersByUserId(USER_ID).isEmpty());
    assertEquals(1, product.getStock());
    assertEquals(2, cartService.getCartByUserId(USER_ID).getItems().get(0).getQuantity());
  }

  @Test
  void checkoutDeductsStockAndCancellationRestoresIt() {
    Product product = productService.getProductById("1");
    int initialStock = product.getStock();
    cartService.addItemToCart(USER_ID, product, 2);

    ResponseEntity<?> checkout = orderController.createOrder(validOrderRequest());

    assertEquals(HttpStatus.CREATED, checkout.getStatusCode());
    Order order = (Order) checkout.getBody();
    assertEquals(initialStock - 2, product.getStock());
    Cart clearedCart = cartService.getCartByUserId(USER_ID);
    assertTrue(clearedCart.getItems().isEmpty());
    assertEquals(0.0, clearedCart.getTotal());

    ResponseEntity<?> cancellation = orderController.cancelOrder(order.getId());

    assertEquals(HttpStatus.OK, cancellation.getStatusCode());
    assertEquals("cancelled", order.getStatus());
    assertEquals(initialStock, product.getStock());
    assertEquals(HttpStatus.BAD_REQUEST,
        orderController.cancelOrder(order.getId()).getStatusCode());
  }

  private OrderRequest validOrderRequest() {
    OrderRequest.Address address = new OrderRequest.Address();
    address.setStreet("1 Main Street");
    address.setCity("Example City");
    address.setState("CA");
    address.setZipCode("90000");
    address.setCountry("US");

    OrderRequest request = new OrderRequest();
    request.setShippingAddress(address);
    request.setPaymentMethod("card");
    return request;
  }
}
