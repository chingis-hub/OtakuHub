package com.chingis.animehub.service

import com.chingis.animehub.entity.Role
import com.chingis.animehub.entity.User
import com.chingis.animehub.repository.UserRepository
import org.springframework.stereotype.Service


@Service
class UserService(
    private val repository: UserRepository
) {

    fun create(user: User) = repository.save(user)


    fun getAll(): List<User> = repository.findAll()

    // В Spring MVC с JPA каждый HTTP-запрос получает поток Tomcat, который остаётся занятымие suspend с Dispatchers.IO лишь перекладывает блокировку на другой поток, не освобождая исходный.
    // В итоге реальной асинхронности нет, и на один запрос расходуются два потока, что не даёт преимуществ.
   fun getById(id: Long) = repository.findById(id).orElse(null)

    fun update(id: Long, updated: User): User? {
        val existingUser = repository.findById(id).orElse(null) ?: return null
        val userToSave = existingUser.copy(
            name = updated.name,
            email = updated.email
        )
        return repository.save(userToSave)
    }

    fun delete(id: Long) = repository.deleteById(id)

    fun change(id: Long) {
        val user = repository.findById(id)
            .orElseThrow { RuntimeException("User not found") }

        user.role = Role.ADMIN
        repository.save(user)
    }
}
