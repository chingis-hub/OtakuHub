package com.chingis.animehub.service

import com.chingis.animehub.dto.create_dto.ReviewCreateDTO
import com.chingis.animehub.dto.update_dto.ReviewUpdateDTO
import com.chingis.animehub.entity.Anime
import com.chingis.animehub.entity.Review
import com.chingis.animehub.entity.User
import com.chingis.animehub.entity.Role
import com.chingis.animehub.repository.AnimeRepository
import com.chingis.animehub.repository.ReviewRepository
import com.chingis.animehub.repository.UserRepository
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
class ReviewServiceTests {

    @Mock
    private lateinit var reviewRepository: ReviewRepository

    @Mock
    private lateinit var animeRepository: AnimeRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var reviewService: ReviewService

    private lateinit var user: User
    private lateinit var anime: Anime
    private lateinit var review: Review

    @BeforeEach
    fun setup() {
        user = User(
            id = 1L,
            name = "testuser",
            email = "test@example.com",
            password = "encodedPassword",
            role = Role.USER
        )

        anime = Anime(
            id = 1L,
            title = "Test Anime",
            description = "Test Description",
            genre = "Action"
        )

        review = Review(
            id = 1L,
            user = user,
            anime = anime,
            content = "Great anime!",
            score = 4
        )

        anime.reviews = mutableListOf(review)
        anime.rating = 4.0
    }

    @Test
    fun `create should add review and update anime rating`() {
        // Given
        val createDTO = ReviewCreateDTO(
            animeTitle = "Test Anime",
            content = "Great anime!",
            score = 4
        )

        whenever(animeRepository.findByTitle("Test Anime")).thenReturn(anime)
        whenever(userRepository.findById(1L)).thenReturn(Optional.of(user))
        whenever(reviewRepository.save(any())).thenReturn(review)
        whenever(animeRepository.save(any())).thenReturn(anime)

        // When
        val result = reviewService.create(createDTO, 1L)

        // Then
        assertThat(result.content).isEqualTo("Great anime!")
        assertThat(result.score).isEqualTo(4.5)

        verify(animeRepository).findByTitle("Test Anime")
        verify(userRepository).findById(1L)
        verify(reviewRepository).save(any())
        verify(animeRepository).save(any())
    }

    @Test
    fun `update should update review and recalculate anime rating`() {
        // Given
        val updateDTO = ReviewUpdateDTO(
            content = "Updated review",
            score = 5
        )

        whenever(reviewRepository.findById(1L)).thenReturn(Optional.of(review))
        whenever(reviewRepository.save(any())).thenReturn(review.apply {
            content = "Updated review"
            score = 5
        })
        whenever(animeRepository.save(any())).thenReturn(anime)

        // When
        val result = reviewService.update(1L, updateDTO, 1L)

        // Then
        assertThat(result.content).isEqualTo("Updated review")
        assertThat(result.score).isEqualTo(5.0)

        verify(reviewRepository).findById(1L)
        verify(reviewRepository).save(any())
        verify(animeRepository).save(any())
    }

    @Test
    fun `update should throw exception when user is not the owner`() {
        // Given
        val updateDTO = ReviewUpdateDTO(
            content = "Updated review",
            score = 5
        )

        whenever(reviewRepository.findById(1L)).thenReturn(Optional.of(review))

        // When/Then
        assertThrows<IllegalArgumentException> {
            reviewService.update(1L, updateDTO, 2L)
        }

        verify(reviewRepository).findById(1L)
        verify(reviewRepository, never()).save(any())
    }

    @Test
    fun `delete should remove review and recalculate anime rating`() {
        // Given
        whenever(reviewRepository.findById(1L)).thenReturn(Optional.of(review))
        whenever(animeRepository.save(any())).thenReturn(anime)

        // When
        reviewService.delete(1L, 1L)

        // Then
        verify(reviewRepository).findById(1L)
        verify(animeRepository).save(any())
        verify(reviewRepository).deleteById(1L)
    }

    @Test
    fun `delete should throw exception when user is not the owner`() {
        // Given
        whenever(reviewRepository.findById(1L)).thenReturn(Optional.of(review))

        // When/Then
        assertThrows<IllegalArgumentException> {
            reviewService.delete(1L, 2L)
        }

        verify(reviewRepository).findById(1L)
        verify(reviewRepository, never()).deleteById(any())
    }
}