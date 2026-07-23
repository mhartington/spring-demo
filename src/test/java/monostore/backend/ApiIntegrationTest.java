package monostore.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {
  @Autowired MockMvc mvc;

  @Test
  void exposesHealthAndProductsWithoutAuthentication() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("E-commerce API is Running"));
    mvc.perform(get("/api/products?category=accessories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.products.length()").value(1));
  }

  @Test
  void protectsCartAndAuthenticatesKnownUser() throws Exception {
    mvc.perform(get("/api/cart")).andExpect(status().isUnauthorized());
    mvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"john@example.com\",\"password\":\"password123\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(1));
  }

  @Test
  void rejectsBadCredentials() throws Exception {
    mvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"john@example.com\",\"password\":\"wrong\"}"))
        .andExpect(status().isUnauthorized());
  }
}
