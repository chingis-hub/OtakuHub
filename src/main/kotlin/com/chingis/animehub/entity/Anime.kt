package com.chingis.animehub.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
@Table(name = "animes")
data class Anime(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var title: String = "",
    var description: String = "",
    var genre: String = "",

    // mappedBy указывает, что сущность Review владеет связью, а именно поле Review.anime
    // fetch = FetchType.EAGER значит, что связанные данные, подгружаются сразу вместе с основной сущностью, без отложенной загрузки

    @OneToMany(mappedBy = "anime", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JsonManagedReference
    var reviews: MutableList<Review> = mutableListOf(),
    var rating: Double = 0.0
)
