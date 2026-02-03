package com.chingis.animehub.controller

import com.chingis.animehub.dto.create_dto.CreateUserDto
import com.chingis.animehub.entity.User
import com.chingis.animehub.service.UserService
import org.springframework.security.access.prepost.PreAuthorize

import org.springframework.web.bind.annotation.*

// Controller принимает HTTP-запросы от клиента (браузер, Postman, фронтенд)
// и возвращает ответы.

@RestController
@RequestMapping("/users")
class UserController(
    private val service: UserService
) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@RequestBody dto: CreateUserDto) : User {
        val user = User(
            name = dto.name,
            email = dto.email
        )
        return service.create(user)
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAll() = service.getAll()


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getById(@PathVariable id: Long) = service.getById(id)

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(@PathVariable id: Long, @RequestBody user: User) = service.update(id, user)


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    fun changeUserRole(@PathVariable id: Long) = service.change(id)
}