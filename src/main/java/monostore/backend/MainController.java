package monostore.backend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @RequestMapping("/")
    public String index() {
        // JsonObject json = new JsonObject();

        
        return "Greetings from Spring Boot!";
    }
}
