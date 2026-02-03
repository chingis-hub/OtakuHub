package com.chingis.animehub.entity

import jakarta.persistence.*

@Entity
@Table(name = "animes")
class Anime(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    var title: String = "",

    @Column(unique = true, nullable = false)
    var description: String = "",

    @Column(nullable = false)
    var genre: String = "",

    // mappedBy указывает, что сущность Review владеет связью, а именно поле Review.anime
    @OneToMany(mappedBy = "anime", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var reviews: MutableList<Review> = mutableListOf(),

    var rating: Double = 0.0,

    // новое поля для картинок тайтлов
    @Column(unique = true, nullable = true)
    var imageUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studio_id")
    var studio: Studio? = null
)
