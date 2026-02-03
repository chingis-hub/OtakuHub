package com.chingis.animehub.service

import com.chingis.animehub.dto.create_dto.CreateStudioDto
import com.chingis.animehub.entity.Studio
import com.chingis.animehub.repository.StudioRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows

@ExtendWith(MockitoExtension::class)
class StudioServiceTests {

    @Mock
    private lateinit var repository: StudioRepository

    @InjectMocks
    private lateinit var studioService: StudioService

    private lateinit var studio: Studio

    @BeforeEach
    fun setup() {
        studio = Studio(
            id = 1L,
            name = "Test Studio",
            description = "Test Description"
        )
    }

    @Test
    fun `create should save studio and return DTO`() {
        // Given
        val createDTO = CreateStudioDto(
            name = "Test Studio",
            description = "Test Description"
        )

        whenever(repository.save(any())).thenReturn(studio)

        // When
        val result = studioService.create(createDTO)

        // Then
        assertThat(result.name).isEqualTo("Test Studio")
        assertThat(result.description).isEqualTo("Test Description")

        verify(repository).save(any())
    }

    @Test
    fun `update should update studio and return DTO`() {
        // Given
        val updateDTO = CreateStudioDto(
            name = "Updated Studio",
            description = "Updated Description"
        )

        whenever(repository.findById(1L)).thenReturn(Optional.of(studio))
        whenever(repository.save(any())).thenReturn(studio.apply {
            name = "Updated Studio"
            description = "Updated Description"
        })

        // When
        val result = studioService.update(1L, updateDTO)

        // Then
        assertThat(result.name).isEqualTo("Updated Studio")
        assertThat(result.description).isEqualTo("Updated Description")

        verify(repository).findById(1L)
        verify(repository).save(any())
    }

    @Test
    fun `update should throw exception when studio not found`() {
        // Given
        val updateDTO = CreateStudioDto(
            name = "Updated Studio",
            description = "Updated Description"
        )

        whenever(repository.findById(1L)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<RuntimeException> {
            studioService.update(1L, updateDTO)
        }

        verify(repository).findById(1L)
        verify(repository, never()).save(any())
    }

    @Test
    fun `delete should call repository deleteById`() {
        // Given
        whenever(repository.existsById(1L)).thenReturn(true)

        // When
        studioService.delete(1L)

        // Then
        verify(repository).existsById(1L)
        verify(repository).deleteById(1L)
    }

    @Test
    fun `delete should throw exception when studio not found`() {
        // Given
        whenever(repository.existsById(1L)).thenReturn(false)

        // When/Then
        assertThrows<RuntimeException> {
            studioService.delete(1L)
        }

        verify(repository).existsById(1L)
        verify(repository, never()).deleteById(any())
    }

    @Test
    fun `getById should return studio DTO`() {
        // Given
        whenever(repository.findById(1L)).thenReturn(Optional.of(studio))

        // When
        val result = studioService.getById(1L)

        // Then
        assertThat(result.name).isEqualTo("Test Studio")
        assertThat(result.description).isEqualTo("Test Description")

        verify(repository).findById(1L)
    }

    @Test
    fun `getById should throw exception when studio not found`() {
        // Given
        whenever(repository.findById(1L)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<RuntimeException> {
            studioService.getById(1L)
        }

        verify(repository).findById(1L)
    }

    @Test
    fun `getAll should return list of studio DTOs`() {
        // Given
        whenever(repository.findAll()).thenReturn(listOf(studio))

        // When
        val result = studioService.getAll()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("Test Studio")

        verify(repository).findAll()
    }
}