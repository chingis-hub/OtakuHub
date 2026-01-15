package com.chingis.animehub.repository

import com.chingis.animehub.entity.Studio
import org.springframework.data.jpa.repository.JpaRepository

interface StudioRepository : JpaRepository<Studio, Long> {
    fun findByName(studio: String): Studio?
}