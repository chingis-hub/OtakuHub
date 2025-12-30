package com.chingis.animehub

import com.chingis.animehub.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

// Authentication — это объект Spring Security, который описывает текущего пользователя в системе.
// Короче говоря кто ты, залогинен ли ты и какие у тебя права
// Нужен чтобы Spring Security не работал с паролями и токенами напрямую
// Обьект считается залогиненным если в нем передать authorities
@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    // Стандартный Spring-сервис: загружает пользователя из БД по username,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        // substring(7) → убирает "Bearer "
        val jwt = authHeader.substring(7)
        val username = jwtService.extractUsername(jwt)

        // Это защита от повторной аутентификации
        if (SecurityContextHolder.getContext().authentication == null) {
            // Загрузка пользователя из БД
            val userDetails = userDetailsService.loadUserByUsername(username)

            // Проверка валидности JWT
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Создание обьекта Authentication
                val authToken = UsernamePasswordAuthenticationToken(
                    // Principal — кто пользователь
                    userDetails,
                    // Credentials - — чем он подтвердил себя (Пароль, JWT)
                    // тут null, так как пароль мы не храним после логина, и JWT уже был проверен
                    null,
                    // Authorities - роли или права пользователя
                    // Без authorities Spring не пустит в защищённые эндпоинты
                    userDetails.authorities
                )
                // Добавление details - это техническая информация о запросе,
                // на авторизацию напрямую не влияет
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                // Регистрирует пользователя в Spring Security, после нее пользователь счи
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        filterChain.doFilter(request, response)
    }
}