package com.example.nvt.configuration;


import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.ArrayList;

@Configuration
public class OpenAPI30Config {

    public OpenAPI30Config(MappingJackson2HttpMessageConverter converter) {
        var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(new MediaType("application", "octet-stream"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
    }
//    @Bean
//    public OpenAPI customizeOpenAPI(){
//        final String securitySchemeName = "bearerAuth";
//        return new OpenAPI()
//                .addSecurityItem(new SecurityRequirement()
//                        .addList(securitySchemeName))
//                .components(new Components()
//                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
//                                .name(securitySchemeName)
//                                .type(SecurityScheme.Type.HTTP)
//                                .scheme("bearer")
//                                .bearerFormat("JWT")));
//    }

    @Bean
    public OpenAPI customizeOpenAPI(){
        final String securitySchemeName = "jwtCookieAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("My API")
                        .version("1.0")
                        .description("API documentation with cookie-based authentication"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name("jwt")
                                .type(SecurityScheme.Type.APIKEY)  // Change to APIKEY (used for cookies)
                                .in(SecurityScheme.In.COOKIE)));   // Use cookies instead of headers
    }

}