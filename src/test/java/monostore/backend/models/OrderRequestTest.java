package monostore.backend.models;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

class OrderRequestTest {

  @Test
  void requiresShippingAndPaymentDetails() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      Validator validator = factory.getValidator();
      OrderRequest request = new OrderRequest();

      assertTrue(validator.validate(request).stream()
          .anyMatch(error -> error.getPropertyPath().toString().equals("shippingAddress")));
      assertTrue(validator.validate(request).stream()
          .anyMatch(error -> error.getPropertyPath().toString().equals("paymentMethod")));

      OrderRequest.Address address = new OrderRequest.Address();
      address.setStreet("1 Main St");
      address.setCity("Springfield");
      address.setState("CA");
      address.setZipCode("90210");
      address.setCountry("US");
      request.setShippingAddress(address);
      request.setPaymentMethod("card");

      assertTrue(validator.validate(request).isEmpty());
    }
  }
}
