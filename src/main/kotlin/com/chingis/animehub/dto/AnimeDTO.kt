package com.chingis.animehub.dto

data class CreateAnimeDto(
    val title: String,
    val description: String,
    val genre: String,
    val studioId: Long
)

data class UpdateAnimeDto(
    val title: String,
    val description: String,
    val genre: String,
    val studioId: Long
)