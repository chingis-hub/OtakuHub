package com.chingis.animehub.controller

import com.chingis.animehub.dto.LoginRequestDto
import com.chingis.animehub.dto.RegisterRequestDto
import com.chingis.animehub.service.AuthService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    suspend fun register(@RequestBody dto: RegisterRequestDto) = authService.register(dto)

    @PostMapping("/login")
    suspend fun login(@RequestBody dto: LoginRequestDto) = authService.login(dto)
}