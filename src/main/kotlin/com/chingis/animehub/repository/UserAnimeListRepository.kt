package com.chingis.animehub.repository

import com.chingis.animehub.entity.UserAnimeList
import com.chingis.animehub.entity.WatchStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAnimeListRepository : JpaRepository<UserAnimeList, Long> {
    fun findByUserIdAndAnimeId(userId: Long, animeId: Long): UserAnimeList?
    fun findAllByUserId(userId: Long): List<UserAnimeList>
    fun findAllByUserIdAndStatus(userId: Long, status: WatchStatus): List<UserAnimeList>
}