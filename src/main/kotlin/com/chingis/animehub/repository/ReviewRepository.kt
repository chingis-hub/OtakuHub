package com.chingis.animehub.repository

import com.chingis.animehub.entity.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository :JpaRepository<Review, Long> {
}