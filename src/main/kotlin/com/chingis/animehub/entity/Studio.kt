package com.chingis.animehub.entity

import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonManagedReference


@Entity
@Table(name = "studios")
data class Studio(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var name: String = "",

    @Column(length = 1000)
    var description: String? = null,

    @OneToMany(mappedBy = "studio", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    val animes: MutableList<Anime> = mutableListOf()
)