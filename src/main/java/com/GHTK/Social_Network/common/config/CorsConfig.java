package com.GHTK.Social_Network.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins(
                    "http://localhost:5500/",
                    "http://localhost:3001/",
                    "http://localhost:3000/",
                    "http://localhost:*",
                    "http://127.0.0.1:3301/"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS")
            .allowedHeaders("*");
  }
}