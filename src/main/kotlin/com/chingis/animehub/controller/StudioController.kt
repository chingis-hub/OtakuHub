package com.chingis.animehub.controller

import com.chingis.animehub.dto.create_dto.CreateStudioDto
import com.chingis.animehub.dto.response_dto.StudioResponseDTO
import com.chingis.animehub.service.StudioService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/studios")
class StudioController(
    private val service: StudioService
) {
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@RequestBody dto: CreateStudioDto): StudioResponseDTO {
        return service.create(dto)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(@PathVariable id: Long, @RequestBody dto: CreateStudioDto): StudioResponseDTO {
        return service.update(id, dto)
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long) {
        service.delete(id)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_STUDIO_READ')")
    fun getById(@PathVariable id: Long): StudioResponseDTO {
        return service.getById(id)
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_STUDIO_READ')")
    fun getAll(): List<StudioResponseDTO> {
        return service.getAll()
    }
}