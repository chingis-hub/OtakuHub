// ReviewController.kt
package com.chingis.animehub.controller

import com.chingis.animehub.dto.CreateReviewDto
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
            content = review.content,
            score = review.score
        )
    }
}