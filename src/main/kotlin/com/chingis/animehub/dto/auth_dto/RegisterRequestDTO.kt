package com.chingis.animehub.dto.auth_dto

data class RegisterRequestDTO(
    val username: String,
    val email: String,
    val password: String
)
