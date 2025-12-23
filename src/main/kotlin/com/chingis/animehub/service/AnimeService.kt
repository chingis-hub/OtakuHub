package com.chingis.animehub.service

import com.chingis.animehub.entity.Anime
import com.chingis.animehub.repository.AnimeRepository
import org.springframework.stereotype.Service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Service
class AnimeService(
    private val repository: AnimeRepository
) {
    suspend fun create(anime: Anime) =
        withContext(Dispatchers.IO) {
            repository.save(anime)
        }

    suspend fun getByTitle(title: String) =
        withContext(Dispatchers.IO) {
            repository.findByTitle(title)
        }

    suspend fun getAll(): List<Anime> = withContext(Dispatchers.IO) {
        repository.findAll()
    }

}
