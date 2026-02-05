package com.chingis.animehub.dto.update_dto

import com.chingis.animehub.entity.WatchStatus

data class UpdateUserAnimeListDTO(
    val status: WatchStatus
)