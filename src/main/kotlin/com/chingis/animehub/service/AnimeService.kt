package com.chingis.animehub.service

import com.chingis.animehub.dto.create_dto.AnimeCreateDTO
import com.chingis.animehub.dto.update_dto.AnimeUpdateDTO
import com.chingis.animehub.entity.Anime
import com.chingis.animehub.repository.AnimeRepository
import com.chingis.animehub.repository.StudioRepository
import com.chingis.animehub.dto.response_dto.AnimeResponseDTO
import com.chingis.animehub.dto.response_dto.AnimeStudioResponseDTO
import com.chingis.animehub.dto.response_dto.ReviewResponseDTO
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class AnimeService(
    private val repository: AnimeRepository,
    private val studioRepository: StudioRepository
) {
    fun create(dto: AnimeCreateDTO): AnimeResponseDTO {
        val studio = studioRepository.findById(dto.studioId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Studio with id '${dto.studioId}' not found") }

        val anime = Anime(
            title = dto.title,
            description = dto.description,
            genre = dto.genre,
            studio = studio
        )

        val savedAnime = repository.save(anime)
        return mapToDTO(savedAnime)
    }

    fun getById(id: Long): AnimeResponseDTO {
        val anime = repository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Anime with id $id not found") }
        return mapToDTO(anime)
    }

    fun getByTitle(title: String): AnimeResponseDTO = mapToDTO(repository.findByTitle(title))

    fun getAll(): List<AnimeResponseDTO> = repository.findAll().map { mapToDTO(it) }

    fun update(id: Long, dto: AnimeUpdateDTO): AnimeResponseDTO {
        val anime = repository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Anime with id $id not found") }

        anime.title = dto.title
        anime.description = dto.description
        anime.genre = dto.genre

        val newStudio = studioRepository.findById(dto.studioId)
            .orElseThrow { RuntimeException("Studio with id '${dto.studioId}' not found") }

        if (anime.studio?.id != newStudio.id) {
            anime.studio?.animes?.remove(anime)

            newStudio.animes.add(anime)
        }

        val updatedAnime = repository.save(anime)
        return mapToDTO(updatedAnime)
    }

    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot delete: Anime with id $id not found")
        }
        repository.deleteById(id)
    }

    fun uploadImage(id: Long, file: MultipartFile): AnimeResponseDTO {
        // Проверка типа файла
        val allowedTypes = listOf("image/jpeg", "image/png", "image/gif")
        if (!allowedTypes.contains(file.contentType)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported file type. Allowed types: JPEG, PNG, GIF")
        }

        val anime = repository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Anime with id $id not found") }

        // папка для сохранения файлов (подпапка anime)
        // dir — это путь к папке uploads/anime
        val dir = Paths.get("uploads", "anime")
        if (!Files.exists(dir)) {
            Files.createDirectories(dir)
        }

        val fileName = "${id}_${file.originalFilename}"

        // resolve(...) склеивает папку и имя файла.
        val filePath = dir.resolve(fileName)
        file.inputStream.use { input -> // гарантирует, что поток будет закрыт автоматически, даже если возникнет ошибка
            // Сохраняем файл на диск
            Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING)
        }

        // Обновляем URL в anime
        anime.imageUrl = "/images/$fileName"

        val savedAnime = repository.save(anime)
        return mapToDTO(savedAnime)
    }

    fun mapToDTO(anime: Anime): AnimeResponseDTO {
        return AnimeResponseDTO(
            id = anime.id,
            title = anime.title,
            imageUrl = anime.imageUrl,
            description = anime.description,
            genre = anime.genre,
            reviews = anime.reviews.map {
                ReviewResponseDTO(
                    id = it.id,
                    content = it.content,
                    score = it.score
                )
            },
            rating = anime.rating,
            studio = anime.studio?.let {
                AnimeStudioResponseDTO(
                    id = it.id,
                    name = it.name
                )
            } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Anime must have a studio")
        )
    }
}