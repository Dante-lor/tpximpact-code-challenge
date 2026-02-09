package com.tpximpact.shortenerservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig Configuration which allows requests from localhost:3000. This is
 * currently where the web frontend is deployed (with docker-compose) however
 * if we were to deploy this for real, we would likely need to have this 
 * configured.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // allow all endpoints
                .allowedOrigins("http://localhost:3000") // allow the frontend
                .allowedMethods("*");
    }
    
}
