package com.chingis.animehub.dto

data class CreateReviewDto(
    val animeTitle: String,
    val content: String,
    val score: Int
)
