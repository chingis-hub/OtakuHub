package com.chingis.animehub.response_dto


data class AnimeResponseDTO(
    val id: Long,
    val title: String,
    val description: String,
    val genre: String,
    val reviews: List<ReviewResponseDTO>,
    val rating: Double
)