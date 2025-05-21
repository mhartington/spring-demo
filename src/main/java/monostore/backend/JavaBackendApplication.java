package monostore.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "monostore.backend")
public class JavaBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(JavaBackendApplication.class, args);
  }
}
