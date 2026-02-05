package com.chingis.animehub.controller

import com.chingis.animehub.dto.create_dto.CreateUserAnimeListDTO
import com.chingis.animehub.dto.update_dto.UpdateUserAnimeListDTO
import com.chingis.animehub.dto.response_dto.UserAnimeListResponseDTO
import com.chingis.animehub.entity.WatchStatus
import com.chingis.animehub.service.UserAnimeListService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/anime-list")
class UserAnimeListController(
    private val service: UserAnimeListService
) {
    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_LIST_CREATE')")
    fun addAnimeToList(
        @RequestParam userId: Long,
        @RequestBody dto: CreateUserAnimeListDTO
    ): UserAnimeListResponseDTO {
        return service.addAnimeToList(userId, dto)
    }

    @PutMapping("/{animeId}")
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_LIST_UPDATE')")
    fun updateAnimeStatus(
        @RequestParam userId: Long,
        @PathVariable animeId: Long,
        @RequestBody dto: UpdateUserAnimeListDTO
    ): UserAnimeListResponseDTO {
        return service.updateAnimeStatus(userId, animeId, dto)
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_LIST_READ')")
    fun getUserAnimeList(
        @RequestParam userId: Long
    ): List<UserAnimeListResponseDTO> {
        return service.getUserAnimeList(userId)
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_LIST_READ')")
    fun getUserAnimeListByStatus(
        @RequestParam userId: Long,
        @PathVariable status: WatchStatus
    ): List<UserAnimeListResponseDTO> {
        return service.getUserAnimeListByStatus(userId, status)
    }

    @DeleteMapping("/{animeId}")
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_LIST_DELETE')")
    fun removeAnimeFromList(
        @RequestParam userId: Long,
        @PathVariable animeId: Long
    ) {
        service.removeAnimeFromList(userId, animeId)
    }
}