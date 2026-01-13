package com.chingis.animehub.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // все что идёт по /images/** ищи в папке uploads/anime/
        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:uploads/anime/")
    }
}