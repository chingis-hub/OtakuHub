package com.chingis.animehub.entity

import jakarta.persistence.*

@Entity
@Table(name = "reviews")
class Review(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // LAZY не подгружает данные автоматически. Сделаем подгрузку на уровне сервиса (mapToDTO)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id", nullable = false)
    var anime: Anime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    var content: String = "",

    var score: Int = 0
)

