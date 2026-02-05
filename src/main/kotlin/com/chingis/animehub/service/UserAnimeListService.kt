package com.chingis.animehub.service

import com.chingis.animehub.dto.create_dto.CreateUserAnimeListDTO
import com.chingis.animehub.dto.update_dto.UpdateUserAnimeListDTO
import com.chingis.animehub.dto.response_dto.UserAnimeListResponseDTO
import com.chingis.animehub.entity.UserAnimeList
import com.chingis.animehub.entity.WatchStatus
import com.chingis.animehub.repository.AnimeRepository
import com.chingis.animehub.repository.UserAnimeListRepository
import com.chingis.animehub.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserAnimeListService(
    private val repository: UserAnimeListRepository,
    private val userRepository: UserRepository,
    private val animeRepository: AnimeRepository
) {
    fun addAnimeToList(userId: Long, dto: CreateUserAnimeListDTO): UserAnimeListResponseDTO {
        val user = userRepository.findById(userId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User with id $userId not found") }
        
        val anime = animeRepository.findById(dto.animeId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Anime with id ${dto.animeId} not found") }
        
        // Check if the anime is already in the user's list
        val existingEntry = repository.findByUserIdAndAnimeId(userId, dto.animeId)
        if (existingEntry != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Anime is already in the user's list")
        }
        
        val userAnimeList = UserAnimeList(
            user = user,
            anime = anime,
            status = dto.status
        )
        
        val savedUserAnimeList = repository.save(userAnimeList)
        return mapToDTO(savedUserAnimeList)
    }
    
    fun updateAnimeStatus(userId: Long, animeId: Long, dto: UpdateUserAnimeListDTO): UserAnimeListResponseDTO {
        val userAnimeList = repository.findByUserIdAndAnimeId(userId, animeId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found in user's list")

        userAnimeList.status = dto.status

        val updatedUserAnimeList = repository.save(userAnimeList)
        return mapToDTO(updatedUserAnimeList)
    }
    
    fun getUserAnimeList(userId: Long): List<UserAnimeListResponseDTO> {
        if (!userRepository.existsById(userId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User with id $userId not found")
        }
        
        return repository.findAllByUserId(userId).map { mapToDTO(it) }
    }
    
    fun getUserAnimeListByStatus(userId: Long, status: WatchStatus): List<UserAnimeListResponseDTO> {
        if (!userRepository.existsById(userId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User with id $userId not found")
        }
        
        return repository.findAllByUserIdAndStatus(userId, status).map { mapToDTO(it) }
    }

    fun removeAnimeFromList(userId: Long, animeId: Long) {
        val userAnimeList = repository.findByUserIdAndAnimeId(userId, animeId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found in user's list")
        
        repository.delete(userAnimeList)
    }
    
    private fun mapToDTO(userAnimeList: UserAnimeList): UserAnimeListResponseDTO {
        return UserAnimeListResponseDTO(
            id = userAnimeList.id,
            animeId = userAnimeList.anime.id,
            animeTitle = userAnimeList.anime.title,
            animeImageUrl = userAnimeList.anime.imageUrl,
            status = userAnimeList.status
        )
    }
}