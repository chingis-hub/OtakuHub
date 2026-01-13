package com.chingis.animehub.service

import com.chingis.animehub.entity.Anime
import com.chingis.animehub.repository.AnimeRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class AnimeService(
    private val repository: AnimeRepository
) {
    fun create(anime: Anime): Anime {
        return repository.save(anime)
    }

    fun update(id: Long, anime: Anime): Anime {
        // проверка на существование
        if (!repository.existsById(id)) {
            throw RuntimeException("Anime not found with id: $id")
        }
        return repository.save(anime)
    }

    fun delete(id: Long) {
        repository.deleteById(id)
    }

    fun getByTitle(title: String) = repository.findByTitle(title)

    fun getAll(): List<Anime> = repository.findAll()

    fun uploadImage(id: Long, file: MultipartFile): Anime {

        // Проверка типа файла
        val allowedTypes = listOf("image/jpeg", "image/png", "image/gif")
        if (!allowedTypes.contains(file.contentType)) {
            throw IllegalArgumentException("Unsupported file type. Allowed types: JPEG, PNG, GIF")
        }
        val anime = repository.findById(id).orElseThrow { RuntimeException("Anime not found") }

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

        return repository.save(anime)
    }
}
