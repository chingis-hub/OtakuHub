// ReviewService.kt
package com.chingis.animehub.service

import com.chingis.animehub.dto.CreateReviewDto
import com.chingis.animehub.entity.Review
import com.chingis.animehub.repository.AnimeRepository
import com.chingis.animehub.repository.ReviewRepository
import com.chingis.animehub.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val animeRepository: AnimeRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    suspend fun create(dto: CreateReviewDto, userId: Long): Review = withContext(Dispatchers.IO) {
        val anime = animeRepository.findByTitle(dto.animeTitle)

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User $userId not found") }

        val review = Review(
            user = user,
            anime = anime,
            content = dto.content,
            score = dto.score
        )

        val savedReview = reviewRepository.save(review)
        anime.reviews.add(savedReview)

        anime.rating = if (anime.reviews.isNotEmpty()) {
            anime.reviews.map { it.score }.average()
        } else {
            0.0
        }

        animeRepository.save(anime)
        savedReview
    }


    suspend fun getAllReviews(): List<Review> =
        withContext(Dispatchers.IO) {
        reviewRepository.findAll()
    }
}