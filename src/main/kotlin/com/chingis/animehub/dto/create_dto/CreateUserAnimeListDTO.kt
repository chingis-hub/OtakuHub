package com.chingis.animehub.dto.create_dto

import com.chingis.animehub.entity.WatchStatus

data class CreateUserAnimeListDTO(
    val animeId: Long,
    val status: WatchStatus
)