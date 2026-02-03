package com.chingis.animehub.dto.update_dto

data class AnimeUpdateDTO(
    val title: String,
    val description: String,
    val genre: String,
    val studioId: Long
)