package com.chingis.animehub.dto.response_dto

import com.chingis.animehub.entity.WatchStatus

data class UserAnimeListResponseDTO(
    val id: Long,
    val animeId: Long,
    val animeTitle: String,
    val animeImageUrl: String?,
    val status: WatchStatus,
)
