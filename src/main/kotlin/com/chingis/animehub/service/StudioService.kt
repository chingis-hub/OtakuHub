package com.chingis.animehub.service

import com.chingis.animehub.dto.create_dto.CreateStudioDto
import com.chingis.animehub.entity.Studio
import com.chingis.animehub.repository.StudioRepository
import com.chingis.animehub.dto.response_dto.StudioResponseDTO
import org.springframework.stereotype.Service
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Service
class StudioService(
    private val repository: StudioRepository
) {
    fun create(dto: CreateStudioDto): StudioResponseDTO {
        val studio = Studio(
            name = dto.name,
            description = dto.description
        )
        val savedStudio = repository.save(studio)
        return mapToDTO(savedStudio)
    }

    fun getById(id: Long): StudioResponseDTO {
        val studio = repository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Studio with id $id not found") }
        return mapToDTO(studio)
    }

    fun getAll(): List<StudioResponseDTO> = repository.findAll().map { mapToDTO(it) }

    fun update(id: Long, dto: CreateStudioDto): StudioResponseDTO {
        val studio = repository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Studio with id $id not found") }

        studio.name = dto.name
        studio.description = dto.description

        val updatedStudio = repository.save(studio)
        return mapToDTO(updatedStudio)
    }

    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Studio with id $id not found")
        }
        repository.deleteById(id)
    }

    fun mapToDTO(studio: Studio): StudioResponseDTO {
        return StudioResponseDTO(
            id = studio.id,
            name = studio.name,
            description = studio.description,
            animes = studio.animes.map { it.title }
        )
    }
}