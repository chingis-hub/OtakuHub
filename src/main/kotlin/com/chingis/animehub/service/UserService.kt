package com.chingis.animehub.service

import com.chingis.animehub.entity.User
import com.chingis.animehub.repository.UserRepository
import org.springframework.stereotype.Service


@Service
class UserService(
    private val repository: UserRepository
) {

    fun create(user: User) = repository.save(user)

    fun getAll(): List<User> = repository.findAll()

    fun getById(id: Long) = repository.findById(id).orElse(null)

    fun update(id: Long, updated: User): User? {
        return repository.findById(id).map {
            val user = it.copy(name = updated.name, email = updated.email)
            repository.save(user)
        }.orElse(null)
    }

    fun delete(id: Long) = repository.deleteById(id)
}
