package com.chingis.animehub.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
@Table(name = "animes")
data class Anime(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    var title: String = "",

    @Column(unique = true, nullable = false)
    var description: String = "",

    @Column(nullable = false)
    var genre: String = "",

    // mappedBy указывает, что сущность Review владеет связью, а именно поле Review.anime
    // fetch = FetchType.EAGER значит, что связанные данные, подгружаются сразу вместе с основной сущностью, без отложенной загрузки

    @OneToMany(mappedBy = "anime", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JsonManagedReference
    var reviews: MutableList<Review> = mutableListOf(),

    var rating: Double = 0.0,

    // новое поля для картинок тайтлов
    @Column(unique = true, nullable = true)
    var imageUrl: String? = null,

    @ManyToOne
    @JoinColumn(name = "studio_id")
    @JsonBackReference
    var studio: Studio? = null
)
