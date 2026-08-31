package com.ashutosh.blogapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI blogApiOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Spring Blog REST API")
                        .description(
                                "REST API for managing blog posts, categories and comments. "
                                        + "Includes pagination, sorting, validation and comment moderation."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ashutosh Panwar")));
    }
}