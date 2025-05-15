package monostore.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    registry.addMapping("/**")
      .allowedOrigins("http://localhost:5173", "http://localhost:4200")
      .allowedMethods("GET", "POST")
      .allowedHeaders("Content-Type", "Authorization")
      .exposedHeaders("Content-Length")
      .allowCredentials(true)
      .maxAge(3600);

    // Add more mappings...
  }
}
