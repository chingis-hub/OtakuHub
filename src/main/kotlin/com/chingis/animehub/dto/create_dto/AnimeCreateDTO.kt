package com.chingis.animehub.dto.create_dto

data class CreateAnimeDto(
    val title: String,
    val description: String,
    val genre: String,
    val studioId: Long
)
