package com.chingis.animehub.service

import com.chingis.animehub.entity.Anime
import com.chingis.animehub.repository.AnimeRepository
import org.springframework.stereotype.Service

@Service
class AnimeService(
    private val repository: AnimeRepository
) {
    fun create(anime: Anime): Anime {
        return repository.save(anime)
    }

    fun update(id: Long, anime: Anime): Anime {
        return repository.save(anime)
    }

    fun delete(id: Long) {
        repository.deleteById(id)
    }

    fun getByTitle(title: String) = repository.findByTitle(title)

    fun getAll(): List<Anime> = repository.findAll()

}
