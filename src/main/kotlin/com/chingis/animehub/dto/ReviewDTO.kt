package com.chingis.animehub.dto

data class CreateReviewDto(
    val animeTitle: String,
    val content: String,
    val score: Int
)

data class UpdateReviewDto(
    val content: String,
    val score: Int
)