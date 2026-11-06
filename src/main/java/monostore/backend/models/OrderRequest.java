package monostore.backend.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OrderRequest {

  @NotNull(message = "Shipping address is required")
  @Valid
  private Address shippingAddress;

  @NotBlank(message = "Payment method is required")
  private String paymentMethod;

  @Data
  public static class Address {
    @NotBlank
    private String street;
    @NotBlank
    private String city;
    @NotBlank
    private String state;
    @NotBlank
    private String zipCode;
    @NotBlank
    private String country;
  }
}
