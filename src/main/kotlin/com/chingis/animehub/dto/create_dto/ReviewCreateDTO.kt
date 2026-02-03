package com.chingis.animehub.dto.create_dto

data class ReviewCreateDTO(
    val animeTitle: String,
    val content: String,
    val score: Int
)
