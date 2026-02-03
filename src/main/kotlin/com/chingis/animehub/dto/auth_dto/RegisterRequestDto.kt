package com.chingis.animehub.dto.auth_dto

data class RegisterRequestDto(
    val username: String,
    val email: String,
    val password: String
)
