package com.chingis.animehub.controller

import com.chingis.animehub.dto.CreateUserDto
import com.chingis.animehub.entity.User
import com.chingis.animehub.service.UserService

import org.springframework.web.bind.annotation.*

// Controller принимает HTTP-запросы от клиента (браузер, Postman, фронтенд)
// и возвращает ответы.

@RestController
@RequestMapping("/users")
class UserController(
    private val service: UserService
) {

    @PostMapping
    fun create(@RequestBody dto: CreateUserDto) : User {
        val user = User(
            name = dto.name,
            email = dto.email
        )
        return service.create(user)
    }

    @GetMapping
    fun getAll() = service.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = service.getById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody user: User) =
        service.update(id, user)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}