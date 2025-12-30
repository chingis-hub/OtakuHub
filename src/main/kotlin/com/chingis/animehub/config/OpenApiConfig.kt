package com.chingis.animehub.config

import io.swagger.v3.oas.models.*
import io.swagger.v3.oas.models.security.*
import org.springframework.context.annotation.*



// Настройка документацию API и добавление Bearer JWT авторизацию в Swagger UI
@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .components(
                Components().addSecuritySchemes(
                    "bearer-jwt",
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            )
            .addSecurityItem(
                SecurityRequirement().addList("bearer-jwt")
            )
    }
}