// ReviewService.kt
package com.chingis.animehub.service

import com.chingis.animehub.dto.*
import com.chingis.animehub.entity.Review
import com.chingis.animehub.repository.*


import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val animeRepository: AnimeRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun create(dto: CreateReviewDto, userId: Long): Review {
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
        anime.rating = anime.reviews.map { it.score }.average()
        animeRepository.save(anime)

        return savedReview
    }

    @Transactional
    fun update(reviewId: Long, dto: UpdateReviewDto, userId: Long): Review {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { IllegalArgumentException("Review $reviewId not found") }

        // является ли юзер владельцем этого отзыва
        if (review.user?.id != userId) {
            throw IllegalArgumentException("User $userId is not authorized to update this review")
        }

        // обновляем поля
        review.content = dto.content
        review.score = dto.score

        // сохраняем в бд отзыв
        val savedReview = reviewRepository.save(review)

        // обновляем рейтинг
        val anime = review.anime
        if (anime != null) {
            anime.rating = anime.reviews.map { it.score }.average()
            // сохраняем в бд аниме с новым отзывом
            animeRepository.save(anime)
        }

        return savedReview
    }

    @Transactional
    fun delete(reviewId: Long, userId: Long) {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { IllegalArgumentException("Review $reviewId not found") }

        // является ли юзер владельцем этого отзыва
        if (review.user?.id != userId) {
            throw IllegalArgumentException("User $userId is not authorized to delete this review")
        }

        // убираем отзыв
        val anime = review.anime
        if (anime != null) {
            // убираем отзыв из списка аниме
            anime.reviews.remove(review)
            // пересчитываем рейтинг
            anime.rating = if (anime.reviews.isEmpty()) 0.0 else anime.reviews.map { it.score }.average()
            animeRepository.save(anime)
        }

        // удаляем отзыв из бд
        reviewRepository.deleteById(reviewId)
    }

}