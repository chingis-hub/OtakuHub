package com.chingis.animehub.service

import com.chingis.animehub.dto.CreateStudioDto
import com.chingis.animehub.entity.Studio
import com.chingis.animehub.repository.StudioRepository
import org.springframework.stereotype.Service

@Service
class StudioService(
    private val repository: StudioRepository
) {
    fun create(dto: CreateStudioDto): Studio {
        val studio = Studio(
            name = dto.name,
            description = dto.description
        )
        return repository.save(studio)
    }

    fun update(id: Long, dto: CreateStudioDto): Studio {

        val studio = repository.findById(id)
            .orElseThrow { RuntimeException("Studio not found") }

        studio.name = dto.name
        studio.description = dto.description

        return studio
    }

    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw RuntimeException("Studio not found")
        }
        repository.deleteById(id)
    }

    fun getById(id: Long): Studio {
        return repository.findById(id)
            .orElseThrow { RuntimeException("Studio not found") }
    }

    fun getAll(): List<Studio> = repository.findAll()

}