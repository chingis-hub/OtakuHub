package com.chingis.animehub.controller

import com.chingis.animehub.dto.CreateStudioDto
import com.chingis.animehub.entity.Studio
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
    fun create(@RequestBody dto: CreateStudioDto): Studio {
        return service.create(dto)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(@PathVariable id: Long, @RequestBody dto: CreateStudioDto): Studio {
        return service.update(id, dto)
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long) {
        service.delete(id)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getById(@PathVariable id: Long): Studio {
        return service.getById(id)
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAll(): List<Studio> {
        return service.getAll()
    }
}