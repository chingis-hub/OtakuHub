package com.chingis.animehub.config

import com.chingis.animehub.JwtAuthenticationFilter
import org.springframework.context.annotation.*
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthenticationFilter
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    // Говорит, какие фильтры, в каком порядке и с какими правилами работают.
    // Без него Spring использовал бы дефолтные настройки.
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .headers { headers ->
                headers.frameOptions { it.disable() }
            }
            .authorizeHttpRequests { auth ->
                auth
                    // запросы пропускаются без Authentication
                    .requestMatchers("/auth/**", "/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    // разрешаем анонимам исп методы чтения аниме
                    .requestMatchers(HttpMethod.GET, "/animes/**").permitAll()
                    // все остальные требуют Authentication
                    .anyRequest().authenticated()
            }
            // Сервер не хранит Authentication или другую информацию о пользователе между запросами
            // так как смысла нет помнить инфу предудыших запросов поэтому и Stateless
            // из этого следует что нет нагрузки на память из-за пользователей
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}