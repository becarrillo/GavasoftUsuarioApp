package com.microservices.usuarioapp.configs;

import java.time.Duration;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer, WebMvcRegistrations {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/public", "classpath:/static/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders(
                        "Content-Type",
                        "X-requested-with",
                        "accept", "Origin", "Vary",
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers")
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE")
                .exposedHeaders("Access-Control-Allow-Origin", "Vary", "Access-Control-Max-Age");
    }
}
