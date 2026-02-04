// ReviewService.kt
package com.chingis.animehub.service

import com.chingis.animehub.dto.create_dto.ReviewCreateDTO
import com.chingis.animehub.dto.update_dto.ReviewUpdateDTO
import com.chingis.animehub.entity.Review
import com.chingis.animehub.repository.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val animeRepository: AnimeRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun create(dto: ReviewCreateDTO, userId: Long): Review {
        val anime = animeRepository.findByTitle(dto.animeTitle)

        val user = userRepository.findById(userId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User $userId not found") }

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
    fun update(reviewId: Long, dto: ReviewUpdateDTO, userId: Long): Review {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Review $reviewId not found") }

        // является ли юзер владельцем этого отзыва
        if (review.user.id != userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "User $userId is not authorized to update this review")
        }

        // обновляем поля
        review.content = dto.content
        review.score = dto.score

        // сохраняем в бд отзыв
        val savedReview = reviewRepository.save(review)

        // обновляем рейтинг
        val anime = review.anime

        anime.rating = anime.reviews.map { it.score }.average()

        // сохраняем в бд аниме с новым отзывом
        animeRepository.save(anime)


        return savedReview
    }

    @Transactional
    fun delete(reviewId: Long, userId: Long) {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Review $reviewId not found") }

        // является ли юзер владельцем этого отзыва
        if (review.user.id != userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "User $userId is not authorized to delete this review")
        }

        // убираем отзыв
        val anime = review.anime

        // убираем отзыв из списка аниме
        anime.reviews.remove(review)

        // пересчитываем рейтинг
        anime.rating = if (anime.reviews.isEmpty()) 0.0 else anime.reviews.map { it.score }.average()

        animeRepository.save(anime)

        // удаляем отзыв из бд
        reviewRepository.deleteById(reviewId)
    }

}