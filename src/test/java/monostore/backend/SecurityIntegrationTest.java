package monostore.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void protectsPrivateEndpointsAndAllowsPublicCatalogAccess() throws Exception {
    mockMvc.perform(get("/api/cart"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(get("/api/products"))
        .andExpect(status().isOk());
  }

  @Test
  void loginEstablishesSessionAndLogoutEndsIt() throws Exception {
    MvcResult login = mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"john@example.com","password":"password123"}
                """))
        .andExpect(status().isOk())
        .andReturn();
    MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
    assertNotNull(session);

    mockMvc.perform(get("/api/users/profile").session(session))
        .andExpect(status().isOk());
    mockMvc.perform(post("/api/users/logout")
            .session(session))
        .andExpect(status().isOk());
    mockMvc.perform(get("/api/users/profile"))
        .andExpect(status().isUnauthorized());
  }
}
