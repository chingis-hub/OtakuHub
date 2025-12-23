package com.chingis.animehub.entity

import jakarta.persistence.*


@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var name: String = "",
    var email: String = ""
)