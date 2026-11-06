package monostore.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import monostore.backend.models.Cart;
import monostore.backend.models.Order;
import monostore.backend.models.OrderRequest;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

  @Test
  void createsDistinctOrdersAndScopesLookupsToTheOwner() {
    OrderService orderService = new OrderService();
    Cart cart = new Cart();
    cart.setTotal(25.50);

    Order first = orderService.createOrder("user-1", cart, new OrderRequest());
    Order second = orderService.createOrder("user-1", cart, new OrderRequest());

    assertNotEquals(first.getId(), second.getId());
    assertEquals("pending", first.getStatus());
    assertEquals(25.50, first.getTotal(), 0.001);
    assertTrue(orderService.getOrderByIdAndUserId(first.getId(), "user-1").isPresent());
    assertFalse(orderService.getOrderByIdAndUserId(first.getId(), "user-2").isPresent());
    assertEquals(2, orderService.getOrdersByUserId("user-1").size());
  }
}
