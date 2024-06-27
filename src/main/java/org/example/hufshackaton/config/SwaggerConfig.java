package org.example.hufshackaton.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Management API")
                        .version("1.0")
                        .description("API for managing users and their profile images.")
                        .contact(new Contact()
                                .name("HyunWoo9930")
                                .url("https://github.com/HyunWoo9930/HufsHackaton")
                                .email("hw62459930@gmail.com")));
    }
}
