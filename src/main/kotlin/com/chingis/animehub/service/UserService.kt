package com.chingis.animehub.service

import com.chingis.animehub.entity.User
import com.chingis.animehub.repository.UserRepository
import org.springframework.stereotype.Service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Service
class UserService(
    private val repository: UserRepository
) {

    suspend fun create(user: User) =
        withContext(Dispatchers.IO) {
            repository.save(user)
        }

    suspend fun getAll(): List<User> =
        withContext(Dispatchers.IO) {
            repository.findAll()
        }

    // repository.findById — блокирующий вызов. любые методы JPA, типа save, delete, findAll — синхронные и блокирующие.
    // withContext(Dispatchers.IO) выполняет его в отдельном потоке, основной поток сервера
    suspend fun getById(id: Long) =
        withContext(Dispatchers.IO) {
            repository.findById(id).orElse(null)
        }

    suspend fun update(id: Long, updated: User): User? = withContext(Dispatchers.IO) {
        repository.findById(id).map {
            val user = it.copy(name = updated.name, email = updated.email)
            repository.save(user)
        }.orElse(null)
    }

    suspend fun delete(id: Long) =
        withContext(Dispatchers.IO) {
            repository.deleteById(id)
        }
}
