package monostore.backend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuthMiddleware> authFilter(AuthMiddleware authMiddleware) {
        FilterRegistrationBean<AuthMiddleware> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authMiddleware);
        registrationBean.addUrlPatterns("/api/cart/*", "/api/orders/*");
        return registrationBean;
    }
}
