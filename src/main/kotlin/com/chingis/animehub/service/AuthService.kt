package com.chingis.animehub.service

import com.chingis.animehub.dto.auth_dto.LoginRequestDTO
import com.chingis.animehub.dto.auth_dto.RegisterRequestDTO
import com.chingis.animehub.entity.User
import com.chingis.animehub.repository.UserRepository
import com.chingis.animehub.dto.response_dto.JwtResponseDTO
import com.chingis.animehub.security.Role


import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    fun register(dto: RegisterRequestDTO): User {
        if (userRepository.findByName(dto.username) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Username already exists")
        }

        if (userRepository.findByEmail(dto.email) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already exists")
        }

        val encodedPassword = passwordEncoder.encode(dto.password)
            ?: throw IllegalStateException("Password encoding failed")

        val user = User(
            name = dto.username,
            email = dto.email,
            password = encodedPassword,
            role = Role.USER
        )

        return userRepository.save(user)
    }

    fun login(dto: LoginRequestDTO): JwtResponseDTO {
        val user = userRepository.findByName(dto.username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found")

        if (!passwordEncoder.matches(dto.password, user.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password")
        }

        val token = jwtService.generateToken(user)

        return JwtResponseDTO(token)
    }
}
