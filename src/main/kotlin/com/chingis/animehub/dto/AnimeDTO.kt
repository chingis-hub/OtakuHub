package com.chingis.animehub.dto

data class CreateAnimeDto(
    val title: String,
    val description: String,
    val genre: String,
    val studio: String
)

data class UpdateAnimeDto(
    val title: String,
    val description: String,
    val genre: String,
    val studio: String
)
