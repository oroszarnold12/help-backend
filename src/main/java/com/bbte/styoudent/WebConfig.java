package com.bbte.styoudent;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("capacitor-electron://-",
                        "ionic://localhost",
                        "http://localhost",
                        "http://localhost:8080",
                        "http://localhost:8100",
                        "http://192.168.1.102:8101")
                .allowedMethods("PUT", "DELETE", "GET", "POST")
                .allowCredentials(true);
    }
}