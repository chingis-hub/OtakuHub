package com.chingis.animehub.dto.response_dto

data class AnimeResponseDTO(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val description: String,
    val genre: String,
    val reviews: List<ReviewResponseDTO>,
    val rating: Double,
    val studio: AnimeStudioResponseDTO
)