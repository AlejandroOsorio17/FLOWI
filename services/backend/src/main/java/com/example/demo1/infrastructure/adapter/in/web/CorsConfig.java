package com.example.demo1.infrastructure.adapter.in.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String frontendOrigin;

    public CorsConfig(@Value("${FRONTEND_ORIGIN:http://localhost:3030}") String frontendOrigin) {
        this.frontendOrigin = frontendOrigin;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(frontendOrigin, "http://localhost:3030", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}