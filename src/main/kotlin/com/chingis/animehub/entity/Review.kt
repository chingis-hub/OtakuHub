package com.chingis.animehub.entity

import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonBackReference

@Entity
@Table(name = "reviews")
data class Review (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    // По умолчанию FK ссылается на PK целевой таблицы
    @JoinColumn(name = "anime_id")
    @JsonBackReference
    var anime: Anime? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User? = null,

    var content: String = "",
    var score: Int = 0
)

