package com.chingis.animehub.entity

import jakarta.persistence.*


@Entity
@Table(name = "studios")
class Studio(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var name: String = "",

    @Column(length = 1000)
    var description: String? = null,

    @OneToMany(mappedBy = "studio", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val animes: MutableList<Anime> = mutableListOf()

)