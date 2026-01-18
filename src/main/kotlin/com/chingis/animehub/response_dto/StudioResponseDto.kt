package com.chingis.animehub.response_dto

data class StudioResponseDTO(
    val id: Long,
    val name: String,
    val description: String?,
    val animes: List<String>
)

data class AnimeStudioResponseDTO(
    val id: Long,
    val name: String
)