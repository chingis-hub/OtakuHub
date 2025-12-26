package com.chingis.animehub.service

import com.chingis.animehub.dto.LoginRequestDto
import com.chingis.animehub.dto.RegisterRequestDto
import com.chingis.animehub.entity.User
import com.chingis.animehub.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    suspend fun register(dto: RegisterRequestDto): User = withContext(Dispatchers.IO) {
        if (userRepository.findByName(dto.username) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Username already exists")
        }

        if (userRepository.findByEmail(dto.email) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already exists")
        }

        val encodedPassword = passwordEncoder.encode(dto.password) ?: throw IllegalStateException("Password encoding failed")

        val user = User(
            name = dto.username,
            email = dto.email,
            password = encodedPassword
        )

        userRepository.save(user)
    }

    suspend fun login(dto: LoginRequestDto): String = withContext(Dispatchers.IO) {
        val user = userRepository.findByName(dto.username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found")

        if (!passwordEncoder.matches(dto.password, user.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password")
        }

        "JWT_TOKEN_PLACEHOLDER"
    }
}