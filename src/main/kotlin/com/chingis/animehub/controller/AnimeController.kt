package com.chingis.animehub.controller

import com.chingis.animehub.dto.CreateAnimeDto
import com.chingis.animehub.entity.Anime
import com.chingis.animehub.response_dto.AnimeResponseDTO
import com.chingis.animehub.response_dto.ReviewResponseDTO
import com.chingis.animehub.service.AnimeService
import org.springframework.security.access.prepost.PreAuthorize

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/animes")
class AnimeController(
    private val service: AnimeService
) {
    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_CREATE')")
    fun create(@RequestBody dto: CreateAnimeDto): AnimeResponseDTO {
        val anime = Anime(
            title = dto.title,
            description = dto.description,
            genre = dto.genre
        )
        val savedAnime = service.create(anime)
        return mapToDTO(savedAnime)
    }

    @GetMapping("/title/{title}")
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_READ')")
    fun getByTitle(@PathVariable title: String): AnimeResponseDTO {
        val anime = service.getByTitle(title)
        return mapToDTO(anime)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_UPDATE')")
    fun update(@PathVariable id: Long, @RequestBody dto: CreateAnimeDto): AnimeResponseDTO {
        val anime = Anime(
            id = id,
            title = dto.title,
            description = dto.description,
            genre = dto.genre
        )
        val updatedAnime = service.update(id, anime)
        return mapToDTO(updatedAnime)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_DELETE')")
    fun delete(@PathVariable id: Long) {
        service.delete(id)
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_READ')")
    fun getAll(): List<AnimeResponseDTO> {
        return service.getAll().map { mapToDTO(it) }
    }

    private fun mapToDTO(anime: Anime): AnimeResponseDTO {
        return AnimeResponseDTO(
            id = anime.id,
            title = anime.title,
            description = anime.description,
            genre = anime.genre,
            reviews = anime.reviews.map {
                ReviewResponseDTO(
                    content = it.content,
                    score = it.score
                )
            },
            rating = anime.rating
        )
    }
}