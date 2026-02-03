package com.chingis.animehub.dto.update_dto

data class UpdateAnimeDto(
    val title: String,
    val description: String,
    val genre: String,
    val studioId: Long
)