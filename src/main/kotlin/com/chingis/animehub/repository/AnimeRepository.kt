package com.chingis.animehub.repository

import com.chingis.animehub.entity.Anime
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnimeRepository : JpaRepository<Anime, Long> {
    fun findByTitle(title: String): Anime
}