package com.chingis.animehub.controller

import com.chingis.animehub.dto.auth_dto.LoginRequestDTO
import com.chingis.animehub.dto.auth_dto.RegisterRequestDTO
import com.chingis.animehub.service.AuthService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@RequestBody dto: RegisterRequestDTO) = authService.register(dto)

    @PostMapping("/login")
    fun login(@RequestBody dto: LoginRequestDTO) = authService.login(dto)
}