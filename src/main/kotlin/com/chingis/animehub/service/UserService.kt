package com.chingis.animehub.service

import com.chingis.animehub.security.Role
import com.chingis.animehub.entity.User
import com.chingis.animehub.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    private val repository: UserRepository
) {
    fun create(user: User) = repository.save(user)

    // В Spring MVC с JPA каждый HTTP-запрос получает поток Tomcat, который остаётся занятымие suspend с Dispatchers.IO лишь перекладывает блокировку на другой поток, не освобождая исходный.
    // В итоге реальной асинхронности нет, и на один запрос расходуются два потока, что не даёт преимуществ.
    fun getById(id: Long): User {
        return repository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User with id $id not found") }
    }

    fun getAll(): List<User> = repository.findAll()

    fun update(id: Long, updated: User): User {
        val existingUser = repository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User with id $id not found") }

        val userToSave = existingUser.copy(
            name = updated.name,
            email = updated.email
        )
        return repository.save(userToSave)
    }

    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User with id $id not found")
        }
        repository.deleteById(id)
    }

    fun change(id: Long) {
        val user = repository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User with id $id not found") }

        user.role = Role.ADMIN
        repository.save(user)
    }
}
