package com.chingis.animehub.service

import com.chingis.animehub.dto.create_dto.AnimeCreateDTO
import com.chingis.animehub.dto.update_dto.AnimeUpdateDTO
import com.chingis.animehub.entity.Anime
import com.chingis.animehub.entity.Studio
import com.chingis.animehub.repository.AnimeRepository
import com.chingis.animehub.repository.StudioRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.util.*
import org.assertj.core.api.Assertions.assertThat

@ExtendWith(MockitoExtension::class)
class AnimeServiceTests {

    @Mock
    private lateinit var animeRepository: AnimeRepository

    @Mock
    private lateinit var studioRepository: StudioRepository

    @Mock
    private lateinit var multipartFile: MultipartFile

    @InjectMocks
    private lateinit var animeService: AnimeService

    private lateinit var studio: Studio
    private lateinit var anime: Anime

    @BeforeEach
    fun setup() {
        studio = Studio(id = 1L, name = "Test Studio", description = "Test Description")
        anime = Anime(
            id = 1L,
            title = "Test Anime",
            description = "Test Description",
            genre = "Action",
            studio = studio
        )
    }

    @Test
    fun `create should save anime and return DTO`() {
        // Given
        val createDTO = AnimeCreateDTO(
            title = "Test Anime",
            description = "Test Description",
            genre = "Action",
            studioId = 1L
        )

        whenever(studioRepository.findById(1L)).thenReturn(Optional.of(studio))
        whenever(animeRepository.save(any())).thenReturn(anime)

        // When
        val result = animeService.create(createDTO)

        // Then
        assertThat(result.title).isEqualTo("Test Anime")
        assertThat(result.description).isEqualTo("Test Description")
        assertThat(result.genre).isEqualTo("Action")
        assertThat(result.studio.id).isEqualTo(1L)

        verify(studioRepository).findById(1L)
        verify(animeRepository).save(any())
    }

    @Test
    fun `update should update anime and return DTO`() {
        // Given
        val updateDTO = AnimeUpdateDTO(
            title = "Updated Anime",
            description = "Updated Description",
            genre = "Comedy",
            studioId = 1L
        )

        whenever(animeRepository.findById(1L)).thenReturn(Optional.of(anime))
        whenever(studioRepository.findById(1L)).thenReturn(Optional.of(studio))
        whenever(animeRepository.save(any())).thenReturn(anime.apply {
            title = "Updated Anime"
            description = "Updated Description"
            genre = "Comedy"
        })

        // When
        val result = animeService.update(1L, updateDTO)

        // Then
        assertThat(result.title).isEqualTo("Updated Anime")
        assertThat(result.description).isEqualTo("Updated Description")
        assertThat(result.genre).isEqualTo("Comedy")

        verify(animeRepository).findById(1L)
        verify(studioRepository).findById(1L)
        verify(animeRepository).save(any())
    }

    @Test
    fun `delete should call repository deleteById`() {
        // When
        animeService.delete(1L)

        // Then
        verify(animeRepository).deleteById(1L)
    }

    @Test
    fun `getByTitle should return anime DTO`() {
        // Given
        whenever(animeRepository.findByTitle("Test Anime")).thenReturn(anime)

        // When
        val result = animeService.getByTitle("Test Anime")

        // Then
        assertThat(result.title).isEqualTo("Test Anime")
        verify(animeRepository).findByTitle("Test Anime")
    }

    @Test
    fun `getAll should return list of anime DTOs`() {
        // Given
        whenever(animeRepository.findAll()).thenReturn(listOf(anime))

        // When
        val result = animeService.getAll()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].title).isEqualTo("Test Anime")
        verify(animeRepository).findAll()
    }

    @Test
    fun `uploadImage should update anime image URL and return DTO`() {
        // Given

        whenever(animeRepository.findById(1L)).thenReturn(Optional.of(anime))
        whenever(multipartFile.contentType).thenReturn("image/jpeg")
        whenever(multipartFile.originalFilename).thenReturn("test.jpg")
        whenever(multipartFile.inputStream).thenReturn(ByteArrayInputStream(ByteArray(0)))

        // Using mockStatic for Files and Paths would be ideal, but for simplicity we'll use doNothing
        // In a real test, you might want to use mockito-inline to mock static methods

        whenever(animeRepository.save(any())).thenReturn(anime.apply {
            imageUrl = "/images/1_test.jpg"
        })

        // When
        val result = animeService.uploadImage(1L, multipartFile)

        // Then
        assertThat(result.imageUrl).isEqualTo("/images/1_test.jpg")
        verify(animeRepository).findById(1L)
        verify(animeRepository).save(any())
    }
}