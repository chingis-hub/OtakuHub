// ReviewController.kt
package com.chingis.animehub.controller

import com.chingis.animehub.dto.*
import com.chingis.animehub.response_dto.ReviewResponseDTO
import com.chingis.animehub.service.ReviewService
import org.springframework.security.access.prepost.PreAuthorize

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reviews")
class ReviewController(
    private val service: ReviewService
) {

    @PostMapping
    // Spring Security требует у пользователя определенные возможности,
    // чтобы получить доступ к конкретному эндпоинту.
    @PreAuthorize("hasAuthority('PERMISSION_REVIEW_CREATE')")
    fun create(
        @RequestBody dto: CreateReviewDto,
        @RequestParam userId: Long
    ): ReviewResponseDTO {
        val review = service.create(dto, userId)
        return ReviewResponseDTO(
            id = review.id,
            content = review.content,
            score = review.score
        )
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasAuthority('PERMISSION_REVIEW_UPDATE')")
    fun update(
        @PathVariable reviewId: Long,
        @RequestBody dto: UpdateReviewDto,
        @RequestParam userId: Long
    ): ReviewResponseDTO {
        val review = service.update(reviewId, dto, userId)
        return ReviewResponseDTO(
            id = review.id,
            content = review.content,
            score = review.score
        )
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAuthority('PERMISSION_REVIEW_DELETE')")
    fun delete(
        @PathVariable reviewId: Long,
        @RequestParam userId: Long
    ) {
        service.delete(reviewId, userId)
    }

}