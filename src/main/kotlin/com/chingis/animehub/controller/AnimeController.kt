package com.chingis.animehub.controller

import com.chingis.animehub.dto.create_dto.AnimeCreateDTO
import com.chingis.animehub.dto.update_dto.AnimeUpdateDTO
import com.chingis.animehub.dto.response_dto.AnimeResponseDTO
import com.chingis.animehub.service.AnimeService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/animes")
class AnimeController(
    private val service: AnimeService
) {
    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_CREATE')")
    fun create(@RequestBody dto: AnimeCreateDTO): AnimeResponseDTO {
        return service.create(dto)
    }

    @GetMapping("/title/{title}")
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_READ')")
    fun getByTitle(@PathVariable title: String): AnimeResponseDTO {
        return service.getByTitle(title)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: AnimeUpdateDTO): AnimeResponseDTO {
        return service.update(id, dto)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_DELETE')")
    fun delete(@PathVariable id: Long) {
        service.delete(id)
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_ANIME_READ')")
    fun getAll(): List<AnimeResponseDTO> {
        return service.getAll()
    }

    @PostMapping("/{id}/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('ADMIN')")
    fun uploadImage(@PathVariable id: Long, @RequestPart("file") file: MultipartFile): AnimeResponseDTO {
        return service.uploadImage(id, file)
    }
}